package edu.pe.cibertec.infracciones;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import edu.pe.cibertec.infracciones.exception.InfractorBloqueadoException;
import edu.pe.cibertec.infracciones.model.EstadoMulta;
import edu.pe.cibertec.infracciones.model.Infractor;
import edu.pe.cibertec.infracciones.model.Multa;
import edu.pe.cibertec.infracciones.model.Vehiculo;
import edu.pe.cibertec.infracciones.repository.InfractorRepository;
import edu.pe.cibertec.infracciones.repository.MultaRepository;
import edu.pe.cibertec.infracciones.service.impl.MultaServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Optional;
import java.util.List;

@ExtendWith(MockitoExtension.class)
public class MultaServiceTest {

	@Mock
	private MultaRepository multaRepository;

	@Mock
	private InfractorRepository infractorRepository;

	@InjectMocks
	private MultaServiceImpl multaService;



	@Test
	void transferirMulta_Asignar()
	{
		Long multaId =1L;
		Long infractorBId =2L;

		Vehiculo vehiculoDeLaMulta =new Vehiculo();
		vehiculoDeLaMulta.setId(10L);

		Multa multa =new Multa();
		multa.setId(multaId);
		multa.setEstado(EstadoMulta.PENDIENTE);
		multa.setVehiculo(vehiculoDeLaMulta);

		Infractor infractorB =new Infractor();
		infractorB.setId(infractorBId);
		infractorB.setBloqueado(false);
		infractorB.setVehiculos(List.of(vehiculoDeLaMulta));

		when(multaRepository.findById(multaId)).thenReturn(Optional.of(multa));
		when(infractorRepository.findById(infractorBId)).thenReturn(Optional.of(infractorB));

		multaService.transferirMulta(multaId,infractorBId);

		assertEquals(infractorB,multa.getInfractor(), "La multa transferido al infractor B");

		verify(multaRepository,times(1)).save(multa);
	}



	@Test
	void transferirMulta_Exception()
	{
		Long multaId =1L;
		Long infractorBId =2L;

		Infractor infractorB =new Infractor();
		infractorB.setId(infractorBId);
		infractorB.setBloqueado(true);

		Multa multa =new Multa();
		multa.setId(multaId);
		multa.setEstado(EstadoMulta.PENDIENTE);

		when(multaRepository.findById(multaId)).thenReturn(Optional.of(multa));
		when(infractorRepository.findById(infractorBId)).thenReturn(Optional.of(infractorB));

		ArgumentCaptor<Multa> multaCaptor = ArgumentCaptor.forClass(Multa.class);

		assertThrows(InfractorBloqueadoException.class,()->multaService.transferirMulta(multaId,infractorBId));

		verify(multaRepository).findById(multaId);
		verify(infractorRepository).findById(infractorBId);
		verify(multaRepository,never()).save(multaCaptor.capture());
	}

}
