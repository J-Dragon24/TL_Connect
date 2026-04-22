package com.tl_connect.dev.modules.tuition.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.tl_connect.dev.core.common.exception.InvalidInputException;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "tuition_fee_configs",
    uniqueConstraints = @UniqueConstraint(columnNames = {"academic_year", "cohort"})
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TuitionFeeConfig {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "base_price_per_credit", nullable = false)
    private BigDecimal basePricePerCredit;

    @Column(name = "academic_year", nullable = false)
    private String academicYear;

    @Column(name = "cohort", nullable = false)
    private Integer cohort;
    
    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public static TuitionFeeConfig create(BigDecimal basePricePerCredit, String academicYear, Integer cohort) {
        if (basePricePerCredit == null || academicYear == null || cohort == null) {
            throw new InvalidInputException("Base price per credit, academic year, and cohort are required");
        }
        TuitionFeeConfig tuitionFeeConfig = new TuitionFeeConfig();
        tuitionFeeConfig.basePricePerCredit = basePricePerCredit;
        tuitionFeeConfig.academicYear = academicYear;
        tuitionFeeConfig.cohort = cohort;
        tuitionFeeConfig.createdAt = LocalDateTime.now();
        tuitionFeeConfig.updatedAt = LocalDateTime.now();
        return tuitionFeeConfig;
    }   

    public void update(BigDecimal basePricePerCredit, String academicYear, Integer cohort) {
        if(basePricePerCredit != null) {
            this.basePricePerCredit = basePricePerCredit;
        }
        if(academicYear != null) {
            this.academicYear = academicYear;
        }
        if(cohort != null) {
            this.cohort = cohort;
        }
        this.updatedAt = LocalDateTime.now();
    }
}
