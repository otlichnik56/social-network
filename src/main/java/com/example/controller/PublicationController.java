package com.example.controller;

import com.example.dto.Post;
import com.example.entity.Publication;
import com.example.service.PublicationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.util.List;

@CrossOrigin(origins = "*", maxAge = 4800)
@RestController
@RequestMapping("/post")
public class PublicationController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    private final PublicationService publicationService;

    public PublicationController(PublicationService publicationService) {
        this.publicationService = publicationService;
    }

    @Operation(summary = "Найти публикацию по id",
            description = "введите id публикаций",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = Publication.class)
                            )
                    )
            }
    )
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public Publication getPublication(@PathVariable(name = "id") Long id) {
        logger.info("PublicationController. method getPublication. publication id = " + id);
        return publicationService.getPublication(id);
    }

    @Operation(summary = "Список моих публикации",
            description = "получить список моих публикаций",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = Publication[].class)
                            )
                    )
            }
    )
    @GetMapping("/my")
    @PreAuthorize("isAuthenticated()")
    public List<Publication> getMyPublications(@AuthenticationPrincipal UserDetails userDetails) {
        logger.info("PublicationController. method getMyPublications");
        return publicationService.getMyPublications(userDetails.getUsername());
    }

    @Operation(summary = "Создать публикацию",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "публикация создана",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = Publication.class)
                            )
                    )
            }
    )
    @PostMapping(consumes = "multipart/form-data")
    @PreAuthorize("isAuthenticated()")
    public Publication createPublication(@AuthenticationPrincipal UserDetails userDetails,
                                         @RequestPart(value = "properties") Post post,
                                         @RequestPart(value = "image") MultipartFile file) {
        logger.info("PublicationController. method createPublication");
        return publicationService.createPublication(userDetails.getUsername(), post, file);
    }

    @Operation(summary = "Изменение публикации",
            description = "введите ID публикации для редактирования",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "публикация изменена",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = Publication.class)
                            )
                    )
            }
    )
    @PatchMapping(path = "/{id}", consumes = "multipart/form-data")
    @PreAuthorize("isAuthenticated()")
    public Publication updatePublication(@AuthenticationPrincipal UserDetails userDetails,
                                         @PathVariable(name = "id") Long id,
                                         @RequestPart(value = "properties") Post post,
                                         @RequestPart(value = "image") MultipartFile file) {
        logger.info("PublicationController. method updatePublication. publication id = " + id);
        return publicationService.updatePublication(userDetails.getUsername(), id, post, file);
    }

    @Operation(summary = "Удаление публикации",
            description = "введите ID публикации для удаления",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "публикация успешно удалена",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = Publication.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "публикация не найдена"
                    )
            }
    )
    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> removePublication(@AuthenticationPrincipal UserDetails userDetails,
                                               @PathVariable(name = "id") Long id){
        logger.info("PublicationController. method removePublication. publication id = " + id);
        if (publicationService.removePublication(userDetails.getUsername(), id)) {
            return ResponseEntity.status(200).body("publication successfully deleted");
        } else {
            return ResponseEntity.status(401).build();
        }
    }


    @Operation(summary = "Лента новостей",
            description = "показать ленту новостей, выводит постранично (по умолчанию по 20 записей)",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "последние новости",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = Publication[].class)
                            )
                    )
            }
    )
    @GetMapping("/news")
    @PreAuthorize("isAuthenticated()")
    public List<Publication> getNews(@AuthenticationPrincipal UserDetails userDetails,
                                     @RequestParam(value = "offset", defaultValue = "0") @Min(0) Integer offset,
                                     @RequestParam(value = "limit", defaultValue = "20") @Min(1) @Max(100) Integer limit) {
        return publicationService.getPublicationsMySubscriptions(userDetails.getUsername(), offset, limit);
    }

}
