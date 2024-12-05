package org.duyvu.carbooking.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;
import lombok.Getter;
import lombok.Setter;
import org.duyvu.carbooking.model.RideTransactionStatus;
import org.hibernate.annotations.CreationTimestamp;
import org.locationtech.jts.geom.Point;

@Getter
@Setter
@Entity
@Table(name = "ride_transaction", schema = "car_booking")
public class RideTransaction {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false)
	private Long id;

	@NotNull
	@Column(name = "price", nullable = false)
	private Double price;

	@NotNull
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "customer_id", nullable = false)
	private Customer customer;

	@NotNull
	@Column(name = "start_time", nullable = false)
	@CreationTimestamp
	private Instant startTime;

	@NotNull
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "driver_id", nullable = false)
	private Driver driver;

	@NotNull
	@Column(name = "end_time", nullable = false)
	@CreationTimestamp
	private Instant endTime;

	@Column(name = "start_location", columnDefinition = "point not null")
	private Point startLocation;

	@Column(name = "destination_location", columnDefinition = "point not null")
	private Point destinationLocation;

	@NotNull
	@Column(name = "ride_transaction_status", nullable = false)
	@Enumerated(EnumType.STRING)
	private RideTransactionStatus rideTransactionStatus;
}