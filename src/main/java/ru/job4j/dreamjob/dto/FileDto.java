package ru.job4j.dreamjob.dto;

import java.util.Arrays;
import java.util.Objects;

public class FileDto {

    private String name;

    private byte[] content; /*тут кроется различие. доменная модель хранит путь, а не содержимое*/

    public FileDto(String name, byte[] content) {
        this.name = name;
        this.content = content;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || getClass() != object.getClass()) {
            return false;
        }
        FileDto fileDto = (FileDto) object;
        return Objects.equals(name, fileDto.name) && Objects.deepEquals(content, fileDto.content);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, Arrays.hashCode(content));
    }
}