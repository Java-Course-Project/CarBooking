package org.duyvu.carbooking.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.duyvu.carbooking.configuration.generator.IdGeneratorOrUseExistedId;

@Getter
@Setter
@Entity
@Table(name = "fare", schema = "car_booking")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Fare {
	@Id
	@IdGeneratorOrUseExistedId
	@Column(name = "transportation_type_id", nullable = false)
	private Integer id;

	@MapsId
	@OneToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "transportation_type_id", nullable = false)
	private TransportationType transportationType;

	@NotNull
	@Column(name = "price", nullable = false)
	private Double price;

	@NotNull
	@Column(name = "rush_hour_rate", nullable = false)
	private Double rushHourRate;

	@NotNull
	@Column(name = "normal_hour_rate", nullable = false)
	private Double normalHourRate;

	@NotNull
	@Column(name = "holiday_rate", nullable = false)
	private Double holidayRate;

	@NotNull
	@Column(name = "normal_day_rate", nullable = false)
	private Double normalDayRate;

}