package org.duyvu.carbooking.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "transaction", schema = "car_booking")
public class Transaction implements Serializable {
	private static final long serialVersionUID = 194914684702195789L;

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	@JdbcTypeCode(SqlTypes.VARCHAR)
	private UUID id;

	@NotNull
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "customer_id", nullable = false, referencedColumnName = "id")
	private User customer;

	@NotNull
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "driver_id", nullable = false, referencedColumnName = "id")
	private User driver;

	@NotNull
	@Column(name = "fare", nullable = false)
	private Double fare;

/*
 TODO [Reverse Engineering] create field to map the 'start_point' column
 Available actions: Define target Java type | Uncomment as is | Remove column mapping
    @Column(name = "start_point", columnDefinition = "point not null")
    private Object startPoint;
*/
/*
 TODO [Reverse Engineering] create field to map the 'end_point' column
 Available actions: Define target Java type | Uncomment as is | Remove column mapping
    @Column(name = "end_point", columnDefinition = "point not null")
    private Object endPoint;
*/
}