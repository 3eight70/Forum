//package com.hits.common.Core.Utils;
//
//import com.google.protobuf.ByteString;
//import com.google.protobuf.Timestamp;
//import com.hits.common.Core.Notification.DTO.NotificationDTO;
//import com.hits.common.Core.Notification.Proto.NotificationDTOOuterClass;
//
//import java.time.Instant;
//
//
//public class KafkaUtils {
//    public static byte[] convertDataFromObjectToNotification(Object data){
//        if (data instanceof NotificationDTO notificationDTO){
//            Instant instant = Instant.now();
//            Timestamp timestamp = Timestamp.newBuilder()
//                    .setSeconds(instant.getEpochSecond())
//                    .setNanos(instant.getNano())
//                    .build();
//
//            return NotificationDTOOuterClass.NotificationDTO.newBuilder()
//                    .setContent(notificationDTO.getContent())
//                    .setTitle(notificationDTO.getTitle())
//                    .setCreateTime(timestamp)
//                    .toByteArray();
//        }
//}
