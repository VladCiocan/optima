package com.hartehanks.optimized;

import com.hartehanks.optima.api.COptimaContact;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RecordParser {
    private String inputFile="";
    private final Map<String, FieldDescriptor> fieldDescriptors = new HashMap<>();
    private int recordLength;
    public static class FieldDescriptor {
        String fieldName;
        int startPos;
        int length;

        public FieldDescriptor(String fieldName, int startPos, int length) {
            this.fieldName = fieldName;
            this.startPos = startPos;
            this.length = length;
        }
    }
    public  List<String> extractEntities(String data, int entityLength) {
        List<String> entities = new ArrayList<>();

        for (int i = 0; i <= data.length() - entityLength; i += entityLength) {
            String entity = data.substring(i, i + entityLength);
            entities.add(entity);
        }

        return entities;
    }
    public  List<byte[]> extractRecords(String filePath, int recordLength) throws IOException {
        byte[] fileData = Files.readAllBytes(Paths.get(filePath));
        List<byte[]> records = new ArrayList<>();

        for (int i = 0; i <= fileData.length - recordLength; i += recordLength) {
            byte[] record = new byte[recordLength];
            System.arraycopy(fileData, i, record, 0, recordLength);
            records.add(record);
        }

        return records;
    }
    public byte[] extractPropertyFromRecord(byte[] record, int startPosition, int propertyLength) {
        byte[] property = new byte[propertyLength];
        System.arraycopy(record, startPosition, property, 0, propertyLength);
        return property;
    }
    public Map<String,COptimaContact> extractContacts(String fullnameField,String businessNameField,String countryNameField) {
        this.inputFile = inputFile;
        Map<String,COptimaContact> contacts = new HashMap<>();
        try {
            List<byte[]> extractedRecords=extractRecords(inputFile,recordLength);
            List<Map<String,String>> extractedRecordsMap = new ArrayList<>();
            for (byte[] record : extractedRecords){
                Map<String,String> recordMap = new HashMap<>();
                for(Map.Entry<String,FieldDescriptor> entry:fieldDescriptors.entrySet()){
                    FieldDescriptor descriptor = entry.getValue();
                    byte[] property = extractPropertyFromRecord(record,descriptor.startPos,descriptor.length);
                    String value = new String(property).trim();
                   recordMap.put(descriptor.fieldName,value);
                }
                extractedRecordsMap.add(recordMap);
            }
            contacts = convertMapOfPropertiesToCOptimaContact(extractedRecordsMap,fullnameField,businessNameField,countryNameField);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return contacts;
    }

    public void loadDescriptor(String filePath) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            String currentFieldName = "";
            int startPos = 0, length = 0;
            while ((line = reader.readLine()) != null) {
                if (line.contains("Field is")) {
                    currentFieldName = line.substring("Field is".length()).trim();
                } else if (line.startsWith("  Starts in COLUMN")) {
                    String startPosString = line.substring("  Starts in COLUMN".length()).trim();
                    startPos = Integer.parseInt(startPosString);
                } else if (line.startsWith("  Length is ")) {
                    String lengthString = line.substring("  Length is ".length()).trim();
                    length = Integer.parseInt(lengthString);

                    // Once we have all details for a field, save it to our map
                    if(currentFieldName.isEmpty() || currentFieldName.length() <3) {
                        recordLength= length;
                    }
                    fieldDescriptors.put(currentFieldName, new FieldDescriptor(currentFieldName, startPos, length));
                }
            }
        }
    }
    public List<Map<String, String>> parseRecords(List<String> records) {
        List<Map<String, String>> parsedRecords = new ArrayList<>();
        System.out.println("Number of lines: "+records.size());
        System.out.println("Record length: "+recordLength);
        for (String baseRecord : records) {
            System.out.println("Line length: "+baseRecord.length());
            if(baseRecord.length()>recordLength){
                List<String> recordsList = extractEntities(baseRecord,recordLength);
                System.out.println("Number of records: "+recordsList.size());
                for(String record:recordsList){
                    parsedRecords.add(parseSingleRecord(record));
                }
            }
            else {
                parsedRecords.add(parseSingleRecord(baseRecord));
            }

        }
        System.out.println("Parsed records: "+parsedRecords.size());
        return parsedRecords;
    }
    public  Map<String, String> parseSingleRecord(String record) {
        Map<String, String> parsedRecord = new HashMap<>();
        for (Map.Entry<String, FieldDescriptor> entry : fieldDescriptors.entrySet()) {

            FieldDescriptor descriptor = entry.getValue();

            int endPos = Math.min(descriptor.startPos + descriptor.length, record.length());
            String value = record.substring(descriptor.startPos, endPos);
            parsedRecord.put(descriptor.fieldName, value.trim()); // .trim() to remove trailing spaces
        }
        System.out.println("Parsed record: "+parsedRecord.get("unique_record_id"));
        return parsedRecord;
    }

    public List<Map<String,String>> parseRecs(String descriptorPath, List<String> records)  {

        try {
            loadDescriptor(descriptorPath);
            return parseRecords(records);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    public Map<String,COptimaContact> convertMapOfPropertiesToCOptimaContact(List<Map<String, String>> parsedProps,String fullnameField,String businessNameField,String countryNameField){
        Map<String, COptimaContact> contacts = new HashMap<>();
        for (Map<String, String> parsedRecord : parsedProps) {
            COptimaContact contact = new COptimaContact();
            int error = 0;
            if(parsedRecord.get(fullnameField) == null ) {
                error++;
                System.out.println("Error: Full name is missing from input reccord");

            }
            if (parsedRecord.get(businessNameField) == null ) {
                error++;
                System.out.println("Error: Business name is missing from input reccord");
            }
            if (parsedRecord.get(countryNameField) == null ) {
                error++;
                System.out.println("Error: Country name is missing from input reccord");
            }
            if(error > 0) {
                System.out.println("Error: Skipping record due to missing fields");
                continue;
            }
            contact.setFullName(parsedRecord.get(fullnameField));
            contact.setCountry(parsedRecord.get(countryNameField));
            contact.setCompany(parsedRecord.get(businessNameField));
            if(parsedRecord.get("oraddrl1") != null) {
                contact.setAddressLine1(parsedRecord.get("oraddrl1"));
                contact.setAddressLine2(parsedRecord.get("oraddrl2"));
                contact.setAddressLine3(parsedRecord.get("oraddrl3"));
                contact.setAddressLine4(parsedRecord.get("oraddrl4"));
                contact.setAddressLine5(parsedRecord.get("oraddrl5"));
                contact.setAddressLine6(parsedRecord.get("oraddrl6"));
                contact.setAddressLine7(parsedRecord.get("oraddrl7"));
                contact.setAddressLine8(parsedRecord.get("oraddrl8"));
            } else if(parsedRecord.get("address1") != null) {
                contact.setAddressLine1(parsedRecord.get("address1"));
                contact.setAddressLine2(parsedRecord.get("address2"));
                contact.setAddressLine3(parsedRecord.get("address3"));
                contact.setAddressLine4(parsedRecord.get("address4"));
                contact.setAddressLine5(parsedRecord.get("address5"));
                contact.setAddressLine6(parsedRecord.get("address6"));
                contact.setAddressLine7(parsedRecord.get("address7"));
                contact.setAddressLine8(parsedRecord.get("address8"));

            }
            contact.setOther1(parsedRecord.get("derived_product_key"));
            contact.setRecordID(parsedRecord.get("unique_record_id"));
            contacts.put(contact.getRecordID(), contact);
        }
        return contacts;
    }
    public Map<String, COptimaContact> convertToCOptimaContacts(String fullnameField,String businessNameField,String countryNameField){
        List<String> records = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(inputFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                records.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        List<Map<String, String>> parsedRecords = parseRecords(records);
        Map<String, COptimaContact> contacts = new HashMap<>();
        for (Map<String, String> parsedRecord : parsedRecords) {
            COptimaContact contact = new COptimaContact();
            int error = 0;
            if(parsedRecord.get(fullnameField) == null ) {
                error++;
                System.out.println("Error: Full name is missing from input reccord");

            }
            if (parsedRecord.get(businessNameField) == null ) {
                error++;
                System.out.println("Error: Business name is missing from input reccord");
            }
            if (parsedRecord.get(countryNameField) == null ) {
                error++;
                System.out.println("Error: Country name is missing from input reccord");
            }
            if(error > 0) {
                System.out.println("Error: Skipping record due to missing fields");
                continue;
            }
            contact.setFullName(parsedRecord.get(fullnameField));
            contact.setCountry(parsedRecord.get(countryNameField));
            contact.setCompany(parsedRecord.get(businessNameField));
            if(parsedRecord.get("oraddrl1") != null) {
                contact.setAddressLine1(parsedRecord.get("oraddrl1"));
                contact.setAddressLine2(parsedRecord.get("oraddrl2"));
                contact.setAddressLine3(parsedRecord.get("oraddrl3"));
                contact.setAddressLine4(parsedRecord.get("oraddrl4"));
                contact.setAddressLine5(parsedRecord.get("oraddrl5"));
                contact.setAddressLine6(parsedRecord.get("oraddrl6"));
                contact.setAddressLine7(parsedRecord.get("oraddrl7"));
                contact.setAddressLine8(parsedRecord.get("oraddrl8"));
            } else if(parsedRecord.get("address1") != null) {
                contact.setAddressLine1(parsedRecord.get("address1"));
                contact.setAddressLine2(parsedRecord.get("address2"));
                contact.setAddressLine3(parsedRecord.get("address3"));
                contact.setAddressLine4(parsedRecord.get("address4"));
                contact.setAddressLine5(parsedRecord.get("address5"));
                contact.setAddressLine6(parsedRecord.get("address6"));
                contact.setAddressLine7(parsedRecord.get("address7"));
                contact.setAddressLine8(parsedRecord.get("address8"));

            }
            contact.setOther1(parsedRecord.get("derived_product_key"));
            contact.setRecordID(parsedRecord.get("unique_record_id"));
            contacts.put(contact.getRecordID(), contact);
        }
        return contacts;
    }

    private void printContact(Map<String, String> contacts) {
        for (Map.Entry<String, String> entry : contacts.entrySet()) {
            System.out.println(entry.getKey()+":"+entry.getValue());
        }
    }

    public String getInputFile() {
        return inputFile;
    }

    public void setInputFile(String inputFile) {
        this.inputFile = inputFile;
    }

    public Map<String, FieldDescriptor> getFieldDescriptors() {
        return fieldDescriptors;
    }
}
