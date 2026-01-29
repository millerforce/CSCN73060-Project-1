package com.group1.froggy.app.controllers;

import com.group1.froggy.app.auth.RequireSession;
import com.group1.froggy.app.services.CommentService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Validated
@RestController
@RequestMapping("/comment")
@Tag(name = "Comment Controller", description = "Handles all operations regarding Comments")
@RequiredArgsConstructor
@RequireSession
public class CommentController {

    private final CommentService commentService;
}
