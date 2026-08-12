package bdbe.bdbd.dto.carwash;

import bdbe.bdbd._core.utils.DateUtils;
import bdbe.bdbd.model.Code.DayType;
import bdbe.bdbd.model.carwash.Carwash;
import bdbe.bdbd.model.file.File;
import bdbe.bdbd.model.location.Location;
import bdbe.bdbd.model.member.Member;
import bdbe.bdbd.model.optime.Optime;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.Valid;
import javax.validation.constraints.*;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class CarwashRequest {

    @Getter
    @Setter
    @ToString
    public static class SaveDTO {

        @NotBlank(message = "Name is required.")
        @Size(min = 2, max = 20, message = "Place name must be between 2 and 20 characters.")
        private String name;

        @Valid
        @NotNull(message = "Location is required.")
        private LocationDTO location;

        @NotBlank(message = "Price is required.")
        private String price;

        @Valid
        @NotNull(message = "optime is required.")
        private OperatingTimeDTO optime;

        private List<Long> keywordIdList;

        @NotBlank(message = "Description is required.")
        @Size(max = 200, message = "Description cannot be longer than 200 characters.")
        private String description;

        @NotBlank(message = "Tel is required.")
        private String tel;


        public Carwash toCarwashEntity(Location location, Member member) {
            return Carwash.builder()
                    .name(name)
                    .rate(0)
                    .tel(tel)
                    .des(description)
                    .price(Integer.parseInt(price))
                    .location(location)
                    .member(member)
                    .build();
        }

        public Location toLocationEntity() {

            return Location.builder()
                    .address(location.address)
                    .latitude(location.latitude)
                    .longitude(location.longitude)
                    .build();
        }

        public List<Optime> toOptimeEntities(Carwash carwash) {
            List<Optime> optimeList = new ArrayList<>();

            optimeList.add(Optime.builder()
                    .dayType(DayType.WEEKDAY)
                    .startTime(optime.getWeekday().getStart())
                    .endTime(optime.getWeekday().getEnd())
                    .carwash(carwash)
                    .build());

            optimeList.add(Optime.builder()
                    .dayType(DayType.WEEKEND)
                    .startTime(optime.getWeekend().getStart())
                    .endTime(optime.getWeekend().getEnd())
                    .carwash(carwash)
                    .build());

            return optimeList;
        }
    }

    @Getter
    @Setter
    @ToString
    public static class LocationDTO {

        @NotBlank(message = "Address is required.")
        @Size(min = 5, max = 50, message = "Address must be between 5 and 200 characters.")
        private String address;

        @NotNull(message = "Latitude is required.")
        @DecimalMin(value = "-90.0", message = "Latitude must be greater than or equal to -90.")
        @DecimalMax(value = "90.0", message = "Latitude must be less than or equal to 90.")
        private Double latitude;

        @NotNull(message = "Longitude is required.")
        @DecimalMin(value = "-180.0", message = "Longitude must be greater than or equal to -180.")
        @DecimalMax(value = "180.0", message = "Longitude must be less than or equal to 180.")
        private Double longitude;
    }

    @Getter
    @Setter
    public static class OperatingTimeDTO {

        @Valid
        @NotNull(message = "Weekday is required.")
        private TimeSlot weekday;

        @Valid
        @NotNull(message = "Weekend is required")
        private TimeSlot weekend;

        @Getter
        @Setter
        public static class TimeSlot {

            @NotNull(message = "Start time is required")
            private LocalTime start;

            @NotNull(message = "End time is required")
            private LocalTime end;
        }
    }

    @Getter
    @Setter
    public static class CarwashDistanceDTO {

        @NotNull(message = "ID is required.")
        private Long id;

        @NotBlank(message = "Name is required.")
        @Size(min = 2, max = 20, message = "Place name must be between 2 and 20 characters.")
        private String name;

        @NotNull(message = "Location is required.")
        private Location location;

        @Positive(message = "Distance must be positive.")
        private double distance;

        @NotNull(message = "rate is required.")
        @DecimalMax(value = "5.0", message = "The rating cannot exceed 5 points.")
        private double rate;

        private long reviewCount;

        @NotNull(message = "Price is required.")
        private Integer price;

        @Valid
        private ImageDTO image;

        public CarwashDistanceDTO(Long id, String name, Location location, double distance, double rate, long reviewCount, int price, File file) {
            this.id = id;
            this.name = name;
            this.location = location;
            this.distance = distance;
            this.rate = rate;
            this.reviewCount = reviewCount;
            this.price = price;
            this.image = (file != null) ? new ImageDTO(file) : null;
        }
    }

    @Getter
    @Setter
    @ToString
    public static class ImageDTO {
        @NotNull(message = "Id is required.")
        private Long id;
        @NotBlank(message = "Name is required.")
        private String name;
        @NotBlank(message = "Url is required.")
        private String url;
        @NotBlank(message = "UploadedAt is required.")
        private String uploadedAt;

        public ImageDTO(File file) {
            this.id = file.getId();
            this.name = file.getName();
            this.url = file.getUrl();
            this.uploadedAt = DateUtils.formatDateTime(file.getUploadedAt());
        }
    }

    @Getter
    @Setter
    @ToString
    public static class UserLocationDTO {

        @NotNull(message = "Latitude is required.")
        @DecimalMin(value = "-90.0", message = "Latitude must be greater than or equal to -90.")
        @DecimalMax(value = "90.0", message = "Latitude must be less than or equal to 90.")
        private double latitude;

        @NotNull(message = "Longitude is required.")
        @DecimalMin(value = "-180.0", message = "Longitude must be greater than or equal to -180.")
        @DecimalMax(value = "180.0", message = "Longitude must be less than or equal to 180.")
        private double longitude;
    }

    @Getter
    @Setter
    public static class SearchRequestDTO {

        @NotEmpty(message = "At least one keyword ID is required.")
        private List<Long> keywordIds;

        @NotNull(message = "Latitude is required.")
        @DecimalMin(value = "-90.0", message = "Latitude must be greater than or equal to -90.")
        @DecimalMax(value = "90.0", message = "Latitude must be less than or equal to 90.")
        private double latitude;

        @NotNull(message = "Longitude is required.")
        @DecimalMin(value = "-180.0", message = "Longitude must be greater than or equal to -180.")
        @DecimalMax(value = "180.0", message = "Longitude must be less than or equal to 180.")
        private double longitude;
    }

    @Getter
    @Setter
    @ToString
    public static class updateCarwashDetailsDTO {

        @NotBlank(message = "Name is required.")
        @Size(min = 2, max = 20, message = "Place name must be between 2 and 20 characters.")
        private String name;

        @NotNull(message = "Price is required.")
        private Integer price;

        @NotBlank(message = "Tel is required.")
        private String tel;

        @Valid
        @NotNull(message = "Location is required")
        private updateLocationDTO location;

        @Valid
        @NotNull(message = "Optime is required")
        private updateOperatingTimeDTO optime;

        private List<Long> keywordIdList;

        @NotBlank(message = "Description is required.")
        @Size(max = 200, message = "Description cannot be longer than 200 characters.")
        private String description;
    }

    @Getter
    @Setter
    public static class updateOperatingTimeDTO {

        @Valid
        @NotNull(message = "Weekday is required.")
        private CarwashRequest.updateOperatingTimeDTO.updateTimeSlot weekday;

        @Valid
        @NotNull(message = "Weekend is required")
        private CarwashRequest.updateOperatingTimeDTO.updateTimeSlot weekend;

        @Getter
        @Setter
        public static class updateTimeSlot {

            @NotNull(message = "Start time is required")
            private LocalTime start;

            @NotNull(message = "End time is required")
            private LocalTime end;

        }
    }

    @Getter
    @Setter
    public static class updateLocationDTO {

        @NotBlank(message = "Address is required.")
        @Size(min = 5, max = 50, message = "Address must be between 5 and 200 characters")
        private String address;

        @NotNull(message = "Latitude is required.")
        @DecimalMin(value = "-90.0", message = "Latitude must be greater than or equal to -90")
        @DecimalMax(value = "90.0", message = "Latitude must be less than or equal to 90")
        private Double latitude;

        @NotNull(message = "Longitude is required.")
        @DecimalMin(value = "-180.0", message = "Longitude must be greater than or equal to -180")
        @DecimalMax(value = "180.0", message = "Longitude must be less than or equal to 180")
        private Double longitude;
    }
}
