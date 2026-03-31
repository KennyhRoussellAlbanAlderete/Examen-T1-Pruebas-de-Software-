package edu.pe.cibertec.infracciones;

import edu.pe.cibertec.infracciones.model.EstadoMulta;
import edu.pe.cibertec.infracciones.model.Infractor;
import edu.pe.cibertec.infracciones.model.Multa;
import edu.pe.cibertec.infracciones.model.Vehiculo;
import edu.pe.cibertec.infracciones.repository.InfractorRepository;
import edu.pe.cibertec.infracciones.repository.MultaRepository;
import edu.pe.cibertec.infracciones.service.impl.InfractorServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InfractorServiceTest {

	@Mock
	private MultaRepository multaRepository;

	@Mock
	private InfractorRepository infractorRepository;


	@InjectMocks
	private InfractorServiceImpl infractorService;

	@Test
	void calcularDeuda_ok() {

		Long infractorId = 1L;

		Multa pendiente = new Multa();
		pendiente.setMonto(200.0);
		pendiente.setEstado(EstadoMulta.PENDIENTE);

		Multa vencida =new Multa();
		vencida.setMonto(300.0);
		vencida.setEstado(EstadoMulta.VENCIDA);

		when(multaRepository.findByInfractor_Id(infractorId)).thenReturn(List.of(pendiente, vencida));

		Double deuda = infractorService.calcularDeuda(infractorId);

		assertEquals(545.0, deuda);

		verify(multaRepository).findByInfractor_Id(infractorId);

	}


	@Test
	void desasignarVehiculo_Remover() {

		Long infractorId = 1L;
		Long vehiculoId = 1L;

		Vehiculo vehiculo =new Vehiculo();
		vehiculo.setId(vehiculoId);

		Infractor infractor =new Infractor();
		infractor.setId(infractorId);
		infractor.setVehiculos(new ArrayList<>(List.of(vehiculo)));

		when(infractorRepository.findById(infractorId)).thenReturn(Optional.of(infractor));
		when(multaRepository.existsByInfractorIdAndVehiculoIdAndEstado(infractorId,vehiculoId,EstadoMulta.PENDIENTE)).thenReturn(false);

		infractorService.desasignarVehiculo(infractorId, vehiculoId);

		boolean aunTieneElVehiculo = infractor.getVehiculos().stream().anyMatch(v -> v.getId().equals(vehiculoId));

		assertFalse(aunTieneElVehiculo, "El vehículo debería haber sido eliminado de la lista");

		verify(infractorRepository,times(1)).save(infractor);
	}


}






