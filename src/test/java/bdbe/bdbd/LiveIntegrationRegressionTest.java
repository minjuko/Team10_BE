package bdbe.bdbd;

import bdbe.bdbd.dto.carwash.CarwashRequest;
import bdbe.bdbd.dto.carwash.CarwashResponse;
import bdbe.bdbd.model.Code.DayType;
import bdbe.bdbd.model.carwash.Carwash;
import bdbe.bdbd.model.location.Location;
import bdbe.bdbd.model.member.Member;
import bdbe.bdbd.model.optime.Optime;
import bdbe.bdbd.model.reservation.Reservation;
import bdbe.bdbd.repository.bay.BayJPARepository;
import bdbe.bdbd.repository.carwash.CarwashJPARepository;
import bdbe.bdbd.repository.file.FileJPARepository;
import bdbe.bdbd.repository.location.LocationJPARepository;
import bdbe.bdbd.repository.optime.OptimeJPARepository;
import bdbe.bdbd.repository.reservation.ReservationJPARepository;
import bdbe.bdbd.service.reservation.ReservationService;
import org.junit.jupiter.api.Test;
import org.springframework.data.jpa.repository.Query;

import java.lang.reflect.Method;
import java.time.LocalTime;
import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class LiveIntegrationRegressionTest {

    @Test
    void detailDtoIncludesCoordinatesAndRecommendedDtoIncludesReviewCount() {
        Location location = Location.builder()
                .id(1001L)
                .address("10 Demo-ro")
                .latitude(35.176)
                .longitude(126.91)
                .build();
        Carwash carwash = Carwash.builder().id(1001L).name("Portfolio Day Carwash")
                .location(location).build();
        Optime weekday = Optime.builder().dayType(DayType.WEEKDAY)
                .startTime(LocalTime.of(9, 30)).endTime(LocalTime.of(18, 30)).build();
        Optime weekend = Optime.builder().dayType(DayType.WEEKEND)
                .startTime(LocalTime.of(9, 30)).endTime(LocalTime.of(18, 30)).build();

        CarwashResponse.findByIdDTO detail = new CarwashResponse.findByIdDTO(
                carwash, 1, 2, location, Collections.emptyList(), weekday, weekend, Collections.emptyList());
        CarwashRequest.CarwashDistanceDTO recommended = new CarwashRequest.CarwashDistanceDTO(
                1001L, "Portfolio Day Carwash", location, 1.2, 5.0, 7, 6000, null);

        assertThat(detail.getLocationDTO().getLatitude()).isEqualTo(35.176);
        assertThat(detail.getLocationDTO().getLongitude()).isEqualTo(126.91);
        assertThat(recommended.getReviewCount()).isEqualTo(7);
    }

    @Test
    void deleteAllowsEqualLargeMemberIdsFromDifferentLongInstances() {
        ReservationJPARepository reservations = mock(ReservationJPARepository.class);
        ReservationService service = new ReservationService(
                reservations,
                mock(CarwashJPARepository.class),
                mock(BayJPARepository.class),
                mock(LocationJPARepository.class),
                mock(OptimeJPARepository.class),
                mock(FileJPARepository.class));
        Member ownerFromReservation = Member.builder().id(Long.valueOf("1000")).build();
        Member authenticatedMember = Member.builder().id(Long.valueOf("1000")).build();
        Reservation reservation = Reservation.builder().id(1001L).member(ownerFromReservation).build();
        when(reservations.findById(1001L)).thenReturn(Optional.of(reservation));

        service.delete(1001L, authenticatedMember);

        assertThat(reservation.isDeleted()).isTrue();
    }

    @Test
    void recentReservationQueryExcludesFutureReservationsAndOrdersNewestFirst() throws Exception {
        Method method = ReservationJPARepository.class.getMethod(
                "findByMemberIdJoinFetch", Long.class, org.springframework.data.domain.Pageable.class);
        String query = method.getAnnotation(Query.class).value();

        assertThat(query).contains("r.endTime < CURRENT_TIMESTAMP");
        assertThat(query).contains("ORDER BY r.endTime DESC");
    }
}
