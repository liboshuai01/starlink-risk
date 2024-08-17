package com.liboshuai.starlink.slr.connector.service.event.impl;

import cn.hutool.core.collection.CollUtil;
import com.liboshuai.starlink.slr.admin.api.constants.ChannelConstants;
import com.liboshuai.starlink.slr.admin.api.dto.EventErrorDTO;
import com.liboshuai.starlink.slr.admin.api.dto.EventUploadDTO;
import com.liboshuai.starlink.slr.admin.api.enums.ChannelEnum;
import com.liboshuai.starlink.slr.connector.api.constants.ErrorCodeConstants;
import com.liboshuai.starlink.slr.connector.dao.kafka.provider.EventProvider;
import com.liboshuai.starlink.slr.connector.pojo.vo.event.KafkaInfoVO;
import com.liboshuai.starlink.slr.connector.service.event.EventService;
import com.liboshuai.starlink.slr.connector.service.event.strategy.EventStrategy;
import com.liboshuai.starlink.slr.connector.service.event.strategy.EventStrategyHolder;
import com.liboshuai.starlink.slr.framework.common.exception.util.ServiceExceptionUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.*;
import org.apache.kafka.common.Node;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Slf4j
@Service
public class EventServiceImpl implements EventService {

    @Resource
    private EventProvider eventProvider;

    @Resource
    private EventStrategyHolder eventStrategyHolder;

    @Value("${spring.kafka.producer.bootstrap-servers}")
    private String bootstrapServers;

    /**
     * 获取Kafka信息，包含是否可连接，并获取broker列表、topic列表、消费组列表等
     */
    @Override
    public KafkaInfoVO kafkaInfo() {
        Properties props = new Properties();
        props.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);

        List<String> brokerList = new ArrayList<>();
        List<String> topicList = new ArrayList<>();
        List<String> consumerGroupList = new ArrayList<>();

        try (AdminClient adminClient = AdminClient.create(props)) {
            // 获取集群信息
            DescribeClusterResult clusterResult = adminClient.describeCluster();
            String clusterId = clusterResult.clusterId().get();
            Collection<Node> nodes = clusterResult.nodes().get();
            log.info("Connected to Kafka cluster, Cluster ID: {}, Number of nodes: {}", clusterId, nodes.size());

            for (Node node : nodes) {
                String brokerInfo = String.format("Node ID: %d, Host: %s, Port: %d", node.id(), node.host(), node.port());
                log.info(brokerInfo);
                brokerList.add(brokerInfo);
            }

            // 获取topic列表
            ListTopicsResult topicsResult = adminClient.listTopics();
            Collection<TopicListing> topics = topicsResult.listings().get();
            log.info("Found {} topics:", topics.size());
            for (TopicListing topic : topics) {
                log.info("Topic name: {}", topic.name());
                topicList.add(topic.name());
            }

            // 获取消费组列表
            ListConsumerGroupsResult consumerGroupsResult = adminClient.listConsumerGroups();
            Collection<ConsumerGroupListing> consumerGroups = consumerGroupsResult.all().get();
            log.info("Found {} consumer groups:", consumerGroups.size());
            for (ConsumerGroupListing consumerGroup : consumerGroups) {
                log.info("Consumer group ID: {}", consumerGroup.groupId());
                consumerGroupList.add(consumerGroup.groupId());
            }

            return new KafkaInfoVO(bootstrapServers, true, null, brokerList, topicList, consumerGroupList);
        } catch (InterruptedException | ExecutionException e) {
            log.error("Failed to connect to Kafka cluster", e);
            return new KafkaInfoVO(bootstrapServers, false, e.getMessage(), null, null, null);
        }
    }

    @Async("slrAsyncExecutor")
    @Override
    public List<EventErrorDTO> batchUpload(List<EventUploadDTO> eventUploadDTOList) {
        // 初步检验上送事件数据参数
        List<EventErrorDTO> eventErrorDTOList = validateUploadList(eventUploadDTOList);
        if (!eventErrorDTOList.isEmpty()) {
            return eventErrorDTOList;
        }
        // 检查并过滤字段值为空的数据
        checkFilterNotEmpty(eventUploadDTOList, eventErrorDTOList);
        // 检查并过滤非法渠道的数据
        checkFilterErrorChannel(eventUploadDTOList, eventErrorDTOList);
        // 各渠道特别的数据处理逻辑
        EventStrategy eventStrategy = eventStrategyHolder.getByChannel(eventUploadDTOList.get(0).getChannel());
        eventStrategy.processAfter(eventUploadDTOList, eventErrorDTOList);
        // 推送数据到kafka
        eventProvider.batchSend(eventUploadDTOList);
        return eventErrorDTOList;
    }

    /**
     * 初步检验上送事件数据参数
     */
    private List<EventErrorDTO> validateUploadList(List<EventUploadDTO> eventUploadDTOList) {
        List<EventErrorDTO> eventErrorDTOList = new ArrayList<>();

        // 判断事件数据集合是否为空
        if (CollUtil.isEmpty(eventUploadDTOList)) {
            eventErrorDTOList.add(
                    EventErrorDTO.builder()
                            .reasons(Collections.singletonList("上送事件数据集合必须非空"))
                            .build()
            );
            return eventErrorDTOList;
        }

        int maxSize = 100;
        // 判断单次上送数据集合元素个数超量
        if (eventUploadDTOList.size() > maxSize) {
            eventErrorDTOList.add(
                    EventErrorDTO.builder()
                            .reasons(Collections.singletonList("单次上送事件数据集合元素个数必须小于等于" + maxSize))
                            .build()
            );
            return eventErrorDTOList;
        }

        return eventErrorDTOList;
    }


    /**
     * 检查并过滤非法渠道的数据
     */
    private void checkFilterErrorChannel(List<EventUploadDTO> eventUploadDTOList, List<EventErrorDTO> eventErrorDTOList) {
        int index = 0;
        Iterator<EventUploadDTO> iterator = eventUploadDTOList.iterator();

        // 获取所有合法渠道的code
        Set<String> validChannels = Arrays.stream(ChannelEnum.values())
                .map(ChannelEnum::getCode)
                .collect(Collectors.toSet());

        while (iterator.hasNext()) {
            EventUploadDTO eventUploadDTO = iterator.next();
            List<String> reasons = new ArrayList<>();

            // 检查渠道是否合法
            String channel = eventUploadDTO.getChannel();
            if (!validChannels.contains(channel)) {
                reasons.add("[channel]非法渠道: " + channel);
            }

            if (!reasons.isEmpty()) {
                EventErrorDTO eventErrorDTO = EventErrorDTO.builder()
                        .eventUploadDTO(eventUploadDTO)
                        .index(index)
                        .reasons(reasons)
                        .build();
                eventErrorDTOList.add(eventErrorDTO);

                // 移除非法渠道的对象
                iterator.remove();
            }
            index++;
        }
    }


    /**
     * 检查并过滤字段值为空的数据
     */
    private void checkFilterNotEmpty(List<EventUploadDTO> eventUploadDTOList, List<EventErrorDTO> eventErrorDTOList) {
        int index = 0;
        Iterator<EventUploadDTO> iterator = eventUploadDTOList.iterator();

        while (iterator.hasNext()) {
            EventUploadDTO eventUploadDTO = iterator.next();
            List<String> reasons = new ArrayList<>();

            checkFilterNotEmpty(eventUploadDTO.getUserId(), "[userId]必须非空", reasons);
            checkFilterNotEmpty(eventUploadDTO.getUsername(), "[username]必须非空", reasons);
            checkFilterNotEmpty(eventUploadDTO.getEventId(), "[eventId]必须非空", reasons);
            checkFilterNotEmpty(eventUploadDTO.getEventTime(), "[eventTime]必须非空", reasons);
            checkFilterNotEmpty(eventUploadDTO.getChannel(), "[channel]必须非空", reasons);

            if (!reasons.isEmpty()) {
                EventErrorDTO eventErrorDTO = EventErrorDTO.builder()
                        .eventUploadDTO(eventUploadDTO)
                        .index(index)
                        .reasons(reasons)
                        .build();
                eventErrorDTOList.add(eventErrorDTO);

                // 移除字段值为空的对象
                iterator.remove();
            }
            index++;
        }
    }

    private void checkFilterNotEmpty(String field, String errorMessage, List<String> reasons) {
        if (!StringUtils.hasText(field)) {
            reasons.add(errorMessage);
        }
    }

}
