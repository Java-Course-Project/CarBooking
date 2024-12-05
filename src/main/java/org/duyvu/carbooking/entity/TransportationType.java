package org.duyvu.carbooking.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.LinkedHashSet;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.duyvu.carbooking.configuration.generator.IdGeneratorOrUseExistedId;

@Getter
@Setter
@Entity
@Table(name = "transportation_type", schema = "car_booking")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TransportationType {
	@Id
	@IdGeneratorOrUseExistedId
	@Column(name = "id", nullable = false)
	private Integer id;

	@Size(max = 128)
	@NotNull
	@Column(name = "type", nullable = false, length = 128)
	private String type;

	@OneToMany(mappedBy = "transportationType")
	private Set<Driver> drivers = new LinkedHashSet<>();

	@OneToOne(mappedBy = "transportationType", cascade = CascadeType.ALL)
	private Fare fare;

}