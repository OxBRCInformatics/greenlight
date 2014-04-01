package uk.ac.ox.brc.greenlight

import grails.transaction.Transactional

@Transactional
class PatientService {

	/**
	 * Finds a patient by their NHS number
	 * @param nhsNumber The NHS number to search by
	 * @return The patient object, or null if there was no match
	 */
	Patient findByNHSNumber(String nhsNumber) {
		return Patient.findByNhsNumber(nhsNumber)
	}

	/**
	 * Finds a patient by their hospital number
	 * @param hospitalNumber The hospital number to search by
	 * @return The patient object, or null if there was no match
	 */
	Patient findByHospitalNumber(String hospitalNumber) {
		return Patient.findByHospitalNumber(hospitalNumber)
	}

	/**
	 * Find a patient by their NHS or hospital numbers.
	 * @param id The NHS number or Hospital Number
	 * @return The patient object, or null if there was no match
	 */
	Patient findByNHSOrHospitalNumber(String patientNumber){
		return Patient.findByNhsNumberOrHospitalNumber(patientNumber, patientNumber)
	}
}
