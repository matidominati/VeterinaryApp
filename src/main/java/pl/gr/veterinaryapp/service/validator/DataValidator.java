package pl.gr.veterinaryapp.service.validator;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import pl.gr.veterinaryapp.exception.IncorrectDataException;
import pl.gr.veterinaryapp.exception.ResourceNotFoundException;

import java.time.LocalDate;
import java.util.Optional;

@Component
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DataValidator {
    public static <T> T findByIdOrThrow(Long id, JpaRepository<T, Long> jpaRepository) {
        Optional<T> entity = jpaRepository.findById(id);
        if (entity.isEmpty()) {
            throw new ResourceNotFoundException("Wrong id.");
        }
        return entity.get();
    }

    public static void validateName(String name) {
        if (name == null) {
            throw new IncorrectDataException("Name cannot be null.");
        }
    }

    public static void validateBirthDate(LocalDate birthDate) {
        if (birthDate == null || birthDate.isAfter(LocalDate.now())) {
            throw new IncorrectDataException("Incorrect birth date provided");
        }
    }

    public static void validateNameOrSurname(String name, String surname) {
        if (name == null || surname == null) {
            throw new IncorrectDataException("Name and Surname should not be null.");
        }
    }
}
