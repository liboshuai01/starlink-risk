package com.liboshuai.starlink.slr.connector.service.event.impl;

import cn.hutool.core.collection.CollUtil;
import com.liboshuai.starlink.slr.admin.api.dto.EventErrorDTO;
import com.liboshuai.starlink.slr.admin.api.dto.EventUploadDTO;
import com.liboshuai.starlink.slr.connector.api.constants.ErrorCodeConstants;
import com.liboshuai.starlink.slr.connector.dao.kafka.provider.EventProvider;
import com.liboshuai.starlink.slr.connector.pojo.vo.event.KafkaInfoVO;
import com.liboshuai.starlink.slr.connector.service.event.EventService;
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

@Slf4j
@Service
public class EventServiceImpl implements EventService {

    @Resource
    private EventProvider eventProvider;

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
    public void batchUpload(List<EventUploadDTO> eventUploadDTOList) {
        if (CollUtil.isEmpty(eventUploadDTOList)) {
            throw ServiceExceptionUtil.exception(ErrorCodeConstants.UPLOAD_EVENT_NOT_EMPTY);
        }
        // 限制单次上送元素个数
        int maxSize = 100;
        if (eventUploadDTOList.size() > maxSize) {
            throw ServiceExceptionUtil.exception(ErrorCodeConstants.UPLOAD_EVENT_OVER_MAX, maxSize);
        }
        List<EventErrorDTO> eventErrorDTOList = new ArrayList<>();
        // 检查并过滤字段值为空的数据
        checkFilterNotEmpty(eventUploadDTOList, eventErrorDTOList);
        // 检查并过滤非法渠道的数据
        checkFilterErrorChanel(eventUploadDTOList, eventErrorDTOList);

        eventProvider.batchSend(eventUploadDTOList);
    }

    /**
     * 检查并过滤非法渠道的数据
     */
    private void checkFilterErrorChanel(List<EventUploadDTO> eventUploadDTOList, List<EventErrorDTO> eventErrorDTOList) {

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
