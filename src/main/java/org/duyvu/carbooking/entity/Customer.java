package org.duyvu.carbooking.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.duyvu.carbooking.model.CustomerStatus;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

@Getter
@Setter
@Entity
@Table(name = "customer", schema = "car_booking")
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class Customer extends BaseUser  {
	@NotNull
	@Column(name = "customer_status", nullable = false)
	@Enumerated(EnumType.STRING)
	private CustomerStatus customerStatus;

	@OneToMany(mappedBy = "customer")
	private Set<RideTransaction> rideTransactions = new LinkedHashSet<>();

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return List.of(new SimpleGrantedAuthority("ROLE_CUSTOMER"));
	}
}