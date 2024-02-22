package com.heno.service;

import com.heno.dto.SaleAgreementAddDto;
import com.heno.dto.SaleAgreementEditDto;
import com.heno.dto.mapper.SaleAgreementAddDtoMapper;
import com.heno.dto.mapper.SaleAgreementEditDtoMapper;
import com.heno.model.Agreement;
import com.heno.model.User;
import com.heno.repository.AgreementRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service class for managing sale agreements.
 */
@Service
public class SaleAgreementService {

    private final AgreementRepository agreementRepository;
    private final SaleAgreementAddDtoMapper saleAgreementAddDtoMapper;
    private final SaleAgreementEditDtoMapper saleAgreementEditDtoMapper;

    /**
     * Constructs a SaleAgreementService with the specified dependencies.
     *
     * @param agreementRepository      The repository for managing agreements.
     * @param saleAgreementAddDtoMapper Mapper for converting SaleAgreementAddDto to Agreement.
     * @param saleAgreementEditDtoMapper Mapper for converting SaleAgreementEditDto to Agreement.
     */
    public SaleAgreementService(
            AgreementRepository agreementRepository,
            SaleAgreementAddDtoMapper saleAgreementAddDtoMapper,
            SaleAgreementEditDtoMapper saleAgreementEditDtoMapper
    ) {
        this.agreementRepository = agreementRepository;
        this.saleAgreementAddDtoMapper = saleAgreementAddDtoMapper;
        this.saleAgreementEditDtoMapper = saleAgreementEditDtoMapper;
    }

    /**
     * Retrieves all sale agreements associated with the specified employee.
     *
     * @param employee The employee for whom to retrieve sale agreements.
     * @return A list of sale agreements associated with the specified employee.
     */
    public List<Agreement> findAll(User employee) {
        return agreementRepository.findAllByEmployeeAndTypeOfAgreement(employee, "sale");
    }

    /**
     * Adds a new sale agreement to the system.
     *
     * @param saleAgreementAddDto The DTO containing information for creating the sale agreement.
     * @param employee            The employee associated with the sale agreement.
     */
    public void addAgreement(SaleAgreementAddDto saleAgreementAddDto, User employee) {
        Agreement agreement = saleAgreementAddDtoMapper.apply(saleAgreementAddDto);
        agreement.setEmployee(employee);
        agreementRepository.save(agreement);
    }

    /**
     * Edits an existing sale agreement in the system.
     *
     * @param saleAgreementEditDto The DTO containing information for editing the sale agreement.
     * @param employee             The employee associated with the sale agreement.
     */
    public void editAgreement(SaleAgreementEditDto saleAgreementEditDto, User employee) {
        Agreement agreement = saleAgreementEditDtoMapper.apply(saleAgreementEditDto);
        agreement.setEmployee(employee);
        agreementRepository.save(agreement);
    }
    /**
     * Retrieves all sale agreements.
     *
     * @return A list of sale agreements.
     */
    public List<Agreement> findAll() {
        return agreementRepository.findAllByTypeOfAgreement("sale");
    }
}

