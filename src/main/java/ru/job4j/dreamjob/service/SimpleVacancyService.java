package ru.job4j.dreamjob.service;

import org.springframework.stereotype.Service;
import ru.job4j.dreamjob.dto.FileDto;
import ru.job4j.dreamjob.model.Vacancy;
import ru.job4j.dreamjob.repository.VacancyRepository;

import javax.annotation.concurrent.ThreadSafe;
import java.util.Collection;
import java.util.Optional;

@Service
@ThreadSafe
public class SimpleVacancyService implements VacancyService {

    private final VacancyRepository vacancyRepository;
    private final FileService fileService;

    public SimpleVacancyService(VacancyRepository sql2oVacancyRepository, FileService fileService) {
        this.vacancyRepository = sql2oVacancyRepository;
        this.fileService = fileService;
    }

    @Override
    public Vacancy save(Vacancy vacancy, FileDto image) {
        if (image != null && image.getContent() != null && image.getContent().length > 0) {
            saveNewFile(vacancy, image);
        }
        return vacancyRepository.save(vacancy);
    }

    private void saveNewFile(Vacancy vacancy, FileDto image) {
        var file = fileService.save(image);
        vacancy.setFileId(file.getId());
    }

    @Override
    public boolean deleteById(int id) {
        var fileOptional = findById(id);
        if (fileOptional.isPresent()) {
            vacancyRepository.deleteById(id);
            fileService.deleteById(fileOptional.get().getFileId());
            return true;
        }
        return false;
    }

    @Override
    public boolean update(Vacancy vacancy, FileDto image) {
        /* Если передан новый файл (не null и не пустой)*/
        if (image != null && image.getContent() != null && image.getContent().length > 0) {
            var oldFileId = vacancy.getFileId();
            saveNewFile(vacancy, image);
            boolean isUpdated = vacancyRepository.update(vacancy);
            if (isUpdated) {
                fileService.deleteById(oldFileId);
            }
            return isUpdated;
        }
        /* Если файл не передан или пустой - обновляем только данные вакансии*/
        return vacancyRepository.update(vacancy);
    }

    @Override
    public Optional<Vacancy> findById(int id) {
        return vacancyRepository.findById(id);
    }

    @Override
    public Collection<Vacancy> findAll() {
        return vacancyRepository.findAll();
    }
}