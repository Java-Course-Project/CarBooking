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
import java.io.Serial;
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
@Table(name = "discount", schema = "car_booking")
public class Discount implements Serializable {
	@Serial
	private static final long serialVersionUID = -5369085064870801183L;

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	@JdbcTypeCode(SqlTypes.VARCHAR)
	private UUID id;

	@NotNull
	@Column(name = "fare", nullable = false)
	private Double fare;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "transaction_id", referencedColumnName = "id")
	private Transaction transaction;

	@NotNull
	@Column(name = "is_used", nullable = false)
	private Boolean isUsed = false;

}