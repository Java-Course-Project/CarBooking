package org.duyvu.carbooking.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.Instant;
import java.util.Collection;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.duyvu.carbooking.model.DriverStatus;
import org.hibernate.annotations.UpdateTimestamp;
import org.locationtech.jts.geom.Point;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

@Getter
@Setter
@Entity
@Table(name = "driver", schema = "car_booking")
public class Driver extends BaseUser {
	@Size(max = 128)
	@NotNull
	@Column(name = "driver_license", nullable = false, length = 128)
	private String driverLicense;

	@NotNull
	@ManyToOne(optional = false)
	@JoinColumn(name = "transportation_type_id", nullable = false)
	private TransportationType transportationType;

	@Column(name = "last_updated", nullable = false)
	@UpdateTimestamp
	private Instant lastUpdated;

	@Column(name = "driver_status")
	@NotNull
	@Enumerated(EnumType.STRING)
	private DriverStatus driverStatus;

	@Column(name = "location", columnDefinition = "point")
	private Point location;

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return List.of(new SimpleGrantedAuthority("ROLE_DRIVER"));
	}
}