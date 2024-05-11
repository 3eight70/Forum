package com.hits.notification.Rest.Controllers.Notification;

import com.hits.common.Core.User.DTO.UserDto;
import com.hits.notification.Core.Notification.DTO.NotificationForUserModel;
import com.hits.notification.Core.Notification.Service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static com.hits.common.Core.Consts.GET_NOTIFICATIONS;
import static com.hits.common.Core.Consts.GET_UNREAD_NOTIFICATIONS;

@RestController
@RequiredArgsConstructor
@Tag(name = "Уведомления", description = "Контроллер, отвечающий за работу с уведомлениями")
public class NotificationController {
    private final NotificationService notificationService;

    @GetMapping(GET_NOTIFICATIONS)
    @Operation(
            summary = "Получение уведомлений",
            description = "Позволяет получить список отсортированных уведомлений с пагинацией"
    )
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<Page<NotificationForUserModel>> getNotifications(
            @AuthenticationPrincipal UserDto userDto,
            @ParameterObject @PageableDefault(sort = "createTime", direction = Sort.Direction.DESC) Pageable pageable,
            @RequestParam(name = "search", required = false) String search
            ){
        return notificationService.getNotifications(pageable, userDto, search);
    }

    @GetMapping(GET_UNREAD_NOTIFICATIONS)
    @Operation(
            summary = "Получение количества непрочитанных уведомлений",
            description = "Позволяеть получить количество уведомлений, которые пользователь не прочитал"
    )
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<Integer> getAmountOfUnreadNotifications(@AuthenticationPrincipal UserDto userDto){
        return notificationService.getAmountOfUnreadNotifications(userDto);
    }
}
