package pl.gr.veterinaryapp.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.gr.veterinaryapp.exception.IncorrectDataException;
import pl.gr.veterinaryapp.exception.ResourceNotFoundException;
import pl.gr.veterinaryapp.mapper.VetMapper;
import pl.gr.veterinaryapp.model.dto.VetRequestDto;
import pl.gr.veterinaryapp.model.dto.VetResponseDto;
import pl.gr.veterinaryapp.model.entity.Vet;
import pl.gr.veterinaryapp.repository.VetRepository;
import pl.gr.veterinaryapp.service.impl.VetServiceImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class VetServiceTest {

    private static final Long VET_ID = 1L;
    private VetRepository vetRepository;
    private VetMapper mapper;
    private VetServiceImpl vetService;

    @BeforeEach
    void setup() {
        this.mapper = Mappers.getMapper(VetMapper.class);
        this.vetRepository = Mockito.mock(VetRepository.class);
        this.vetService = new VetServiceImpl(vetRepository, mapper);
    }

    @Test
    void getVetById_WithCorrectId_Returned() {
        Vet vet = new Vet();

        when(vetRepository.findById(anyLong())).thenReturn(Optional.of(vet));

        VetResponseDto vetDto = mapper.map(vet);
        var result = vetService.getVetById(VET_ID);

        assertThat(result)
                .isNotNull()
                .isEqualTo(vetDto);

        verify(vetRepository).findById(eq(VET_ID));
    }

    @Test
    void getVetById_WithWrongId_ExceptionThrown() {
        when(vetRepository.findById(anyLong())).thenReturn(Optional.empty());

        ResourceNotFoundException thrown =
                catchThrowableOfType(() -> vetService.getVetById(VET_ID), ResourceNotFoundException.class);

        assertThat(thrown)
                .hasMessage("Wrong id.");

        verify(vetRepository).findById(eq(VET_ID));
    }

    @Test
    void getAllVets_ReturnVets_Returned() {
        List<Vet> vets = new ArrayList<>();

        when(vetRepository.findAll()).thenReturn(vets);

        var result = vetService.getAllVets();

        assertThat(result)
                .isNotNull()
                .isEqualTo(vets);

        verify(vetRepository).findAll();
    }

    @Test
    void createVet_CorrectData_saved() {
        VetRequestDto request = new VetRequestDto();
        request.setName("test");
        request.setSurname("test");
        Vet vet = new Vet();
        vet.setName("test");
        vet.setSurname("test");

        when(vetRepository.save(any(Vet.class))).thenReturn(vet);

        var vetDto = mapper.map(vet);
        vetRepository.save(vet);
        var result = vetService.createVet(request);

        assertThat(result)
                .isNotNull()
                .isEqualTo(vetDto);

        verify(vetRepository).save(eq(vet));
    }

    @Test
    void createVet_NameNull_ExceptionThrown() {
        VetRequestDto request = new VetRequestDto();
        request.setSurname("test");

        IncorrectDataException thrown =
                catchThrowableOfType(() -> vetService.createVet(request), IncorrectDataException.class);

        assertThat(thrown)
                .hasMessage("Name and Surname should not be null.");

        verifyNoInteractions(vetRepository);
    }

    @Test
    void createVet_SurnameNull_ExceptionThrown() {
        VetRequestDto request = new VetRequestDto();
        request.setName("test");

        IncorrectDataException thrown =
                catchThrowableOfType(() -> vetService.createVet(request), IncorrectDataException.class);

        assertThat(thrown)
                .hasMessage("Name and Surname should not be null.");

        verifyNoInteractions(vetRepository);
    }

    @Test
    void deleteVet_ExistsVet_Deleted() {
        Vet vet = new Vet();

        when(vetRepository.findById(anyLong())).thenReturn(Optional.of(vet));

        vetService.deleteVet(VET_ID);

        verify(vetRepository).findById(eq(VET_ID));
        verify(vetRepository).delete(eq(vet));
    }

    @Test
    void deleteVet_VetNotFound_ThrownException() {
        when(vetRepository.findById(anyLong())).thenReturn(Optional.empty());

        ResourceNotFoundException thrown =
                catchThrowableOfType(() -> vetService.deleteVet(VET_ID), ResourceNotFoundException.class);

        assertThat(thrown)
                .hasMessage("Wrong id.");

        verify(vetRepository).findById(eq(VET_ID));
    }
}
