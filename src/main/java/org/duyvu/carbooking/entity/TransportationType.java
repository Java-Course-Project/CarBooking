package org.duyvu.carbooking.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.LinkedHashSet;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "transportation_type", schema = "car_booking")
public class TransportationType {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false)
	private Integer id;

	@Size(max = 128)
	@NotNull
	@Column(name = "type", nullable = false, length = 128)
	private String type;

	@OneToMany(mappedBy = "transportationType")
	private Set<Driver> drivers = new LinkedHashSet<>();

	@OneToOne(mappedBy = "transportationType")
	private Fare fare;

}