package ru.job4j.dreamjob.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.job4j.dreamjob.dto.FileDto;
import ru.job4j.dreamjob.service.FileService;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class FileControllerTest {

    private FileService fileService;
    private FileController fileController;

    @BeforeEach
    public void initServices() {
        fileService = mock(FileService.class);
        fileController = new FileController(fileService);
    }

    @Test
    public void whenGetByIdAndFileFoundThenReturnOkWithContent() {
        int fileId = 1;
        byte[] content = {1, 2, 3, 4};
        FileDto fileDto = new FileDto("test.txt", content);
        when(fileService.getFileById(fileId)).thenReturn(Optional.of(fileDto));

        var response = fileController.getById(fileId);

        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(response.getBody()).isEqualTo(content);
    }

    @Test
    public void whenGetByIdAndFileNotFoundThenReturnNotFound() {
        int fileId = 1;
        when(fileService.getFileById(fileId)).thenReturn(Optional.empty());

        var response = fileController.getById(fileId);

        assertThat(response.getStatusCodeValue()).isEqualTo(404);
    }

}