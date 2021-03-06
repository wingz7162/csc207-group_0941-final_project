package com.example.triage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.TreeMap;

public class Organizer {

	private TreeMap<String, Patient> hcnToPatient = new TreeMap<String, Patient>();
	private static Organizer instance = null;

	private Organizer(File dir) throws IOException {
		File records = new File(dir, "patient_records.txt");
		File vitals = new File(dir, "patient_vitals.txt");
		File doctimes = new File(dir, "patient_doctimes.txt");
		File prescription = new File(dir, "patient_prescription.txt");
		if (records.exists()) {
			this.populatePatients(records.getPath());
		} else {
			records.createNewFile();
		}
		if (vitals.exists()) {
			this.addPatientData(vitals.getPath());
		} else {
			vitals.createNewFile();
		}
		if (doctimes.exists()) {
			this.readTimesSeenByDoctor(doctimes.getPath());;
		} else {
			doctimes.createNewFile();
		}
		if (prescription.exists()) {
			this.readPrescription(prescription.getPath());
		} else {
			prescription.createNewFile();
		}
	}

	/**
	 * Returns the instance of Organizer. Creates new one if none exist.
	 * 
	 * @param dir
	 *            - The directory where the required files are located.
	 * @return The single instance of the class Organizer.
	 * @throws IOException
	 */
	public static Organizer getInstance(File dir) throws IOException {
		if (instance == null) {
			instance = new Organizer(dir);
		}
		return instance;
	}

	/**
	 * Reads a given file to generate a list of patients.
	 * 
	 * @param path
	 *            - The path of the file to read from.
	 * @throws FileNotFoundException
	 */
	private void populatePatients(String path) throws FileNotFoundException {

		Scanner scanner = new Scanner(new FileInputStream(path));
		ArrayList<Patient> patients = new ArrayList<Patient>();

		String[] patientData;
		String hcn;
		String dob;
		String name;

		while (scanner.hasNextLine()) {
			patientData = scanner.nextLine().split(",");
			hcn = patientData[0];
			name = patientData[1];
			dob = patientData[2];
			patients.add(new Patient(name, dob, hcn));
		}
		scanner.close();

		for (Patient p : patients) {
			hcnToPatient.put(p.hcn, p);
		}

	}

	/**
	 * Reads a file to add generate a list of patients' data.
	 * 
	 * @param path
	 *            - The path of the file to read from.
	 * @throws FileNotFoundException
	 */
	private void addPatientData(String path) throws FileNotFoundException {

		Scanner scanner = new Scanner(new FileInputStream(path));

		Patient patient;
		String[] patientData;
		String[] data;
		String hcn;
		String toa;
		String vitaltime;
		double temp;
		int bloodpressure;
		String measurement;
		int heartrate;

		while (scanner.hasNextLine()) {
			patientData = scanner.nextLine().split(" ");
			hcn = patientData[0];
			patient = hcnToPatient.get(hcn);

			for (int i = 1; i < patientData.length; i++) {
				data = patientData[i].split(",");
				toa = data[0];
				vitaltime = data[1];
				temp = Double.parseDouble(data[2]);
				bloodpressure = Integer.parseInt(data[3]);
				measurement = data[4];
				heartrate = Integer.parseInt(data[5]);
				patient.setVitalsigns(toa, vitaltime, temp, bloodpressure,
						measurement, heartrate);
			}
		}

		scanner.close();

	}

	private void readTimesSeenByDoctor(String path)
			throws FileNotFoundException {

		Scanner scanner = new Scanner(new FileInputStream(path));
		String[] timesSeenByDoctor;
		Patient patient;
		String hcn;
		String times;

		while (scanner.hasNextLine()) {
			String line = scanner.nextLine();
			System.out.println(line);
			timesSeenByDoctor = line.split(" ");
			hcn = timesSeenByDoctor[0];
			if (timesSeenByDoctor.length == 1) {
				times = "";
			} else {
				times = timesSeenByDoctor[1];	
			}
			patient = hcnToPatient.get(hcn);
			patient.setSeenbydoctor(times);
		}
		scanner.close();
	}

	/**
	 * Reads patients' prescriptions from a file and saves them.
	 * 
	 * @param path
	 *            - The path of the file to read from.
	 * @throws FileNotFoundException
	 */
	private void readPrescription(String path) throws FileNotFoundException {

		Scanner scanner = new Scanner(new FileInputStream(path));
		Patient patient;
		String[] prescriptionInfo;
		String hcn;
		String prescription;

		while (scanner.hasNextLine()) {
			prescriptionInfo = scanner.nextLine().split(" ");
			hcn = prescriptionInfo[0];
			prescription = prescriptionInfo[1];
			patient = hcnToPatient.get(hcn);
			patient.setPrescription(prescription);
		}
		scanner.close();

	}

	/**
	 * Writes medical data for all patients to a file.
	 * 
	 * @param outputStream
	 *            - Output file to be written to.
	 * @throws FileNotFoundException
	 */
	public void saveData(FileOutputStream outputStream)
			throws FileNotFoundException {

		String output;
		TreeMap<String, TreeMap<String, ArrayList<Object>>> vitals;

		try {
			for (String hcn : hcnToPatient.keySet()) {
				vitals = hcnToPatient.get(hcn).getVitalsigns();
				output = hcn + " ";
				for (String toa : vitals.keySet()) {
					for (String vitaltime : vitals.get(toa).keySet()) {
						output = output + toa + "," + vitaltime + ",";
						for (Object data : vitals.get(toa).get(vitaltime)) {
							output = output + data + ",";
						}
						output = output + " ";
					}
				}
				output = output.trim();
				outputStream.write((output + System
						.getProperty("line.separator")).getBytes());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/**
	 * Writes all patient prescriptions to a file.
	 * 
	 * @param outputStream
	 *            - Output file to be written to.
	 * @throws FileNotFoundException
	 */
	public void savePrescriptions(FileOutputStream outputStream)
			throws FileNotFoundException {

		String output;
		Patient patient;

		try {
			for (String hcn : hcnToPatient.keySet()) {
				patient = hcnToPatient.get(hcn);
				output = hcn + "," + patient.getPrescription();
				outputStream.write((output + System
						.getProperty("line.separator")).getBytes());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/**
	 * Writes all patient symptom descriptions to a file.
	 * 
	 * @param outputStream
	 *            - Output file to be written to.-
	 * @throws FileNotFoundException
	 */
	public void saveSymptoms(FileOutputStream outputStream)
			throws FileNotFoundException {

		String output;
		Patient patient;
		TreeMap<String, String> symptoms;

		try {
			for (String hcn : hcnToPatient.keySet()) {
				patient = hcnToPatient.get(hcn);
				symptoms = patient.getSymptoms();
				output = hcn + " ";
				for (String time : symptoms.keySet()) {
					output = output + time + "," + symptoms.get(time) + " ";
				}
				output = output.trim();
				outputStream.write((output + System
						.getProperty("line.separator")).getBytes());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void saveTimesSeenByDoctor(FileOutputStream outputStream)
			throws FileNotFoundException {

		String output;
		String doctimes;
		TreeMap<String, String> patienttimes = new TreeMap<String, String>();

		for (String hcn : hcnToPatient.keySet()) {
			String times = hcnToPatient.get(hcn).getSeenbydoctor();
			patienttimes.put(hcn, times);
		}
		try {
			for (String hcn : patienttimes.keySet()) {
				doctimes = patienttimes.get(hcn);
				output = hcn + " " + doctimes + " ";

				outputStream.write((output + System
						.getProperty("line.separator")).getBytes());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/**
	 * Writes all patients with their personal information to a file.
	 * 
	 * @param outputStream
	 *            - Output file to be written to.
	 * @throws FileNotFoundException
	 */
	public void recordPatients(FileOutputStream outputStream)
			throws FileNotFoundException {

		String output;
		Patient patient;

		try {
			for (String hcn : this.hcnToPatient.keySet()) {
				patient = this.hcnToPatient.get(hcn);
				output = hcn + "," + patient.name + "," + patient.dob;
				outputStream.write((output + System
						.getProperty("line.separator")).getBytes());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/**
	 * Adds new patient to the map of patients.
	 * 
	 * @param p
	 *            - The new patient to be a added.
	 */
	public void addPatient(Patient p) {
		this.hcnToPatient.put(p.hcn, p);
	}

	/**
	 * Returns the Map of patients.
	 * 
	 * @return A Map of healthcard numbers to their respective patients.
	 * @throws FileNotFoundException
	 */
	public TreeMap<String, Patient> getHcnToPatient()
			throws FileNotFoundException {
		return this.hcnToPatient;
	}

	public static void main(String[] args) throws IOException {
		File dir = new File("C:\\Users\\Anders\\Desktop");
		Nurse nurse = new Nurse(dir);
		nurse.lookupPatient("111111");
		nurse.setTimeSeenByDoctor("2014-11-27 11:45");
		Nurse nurse1 = new Nurse(dir);
		nurse1.lookupPatient("111111");
		System.out.println(nurse1.getTimeSeenByDoctor());
	}
}