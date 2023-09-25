package com.hartehanks;

import com.hartehanks.optima.api.*;
import java.util.*;
import java.io.*;
import java.awt.event.*;
import javax.swing.*;
import com.hartehanks.dev.io.*;
import com.hartehanks.dev.misc.*;
import com.hartehanks.dev.app.*;

//
// the GlobalQueueFinisher is a runnable Thread (run by the GlobalFinisher)
// that is
// responsible for finalising output data prepared by the GlobalChild.
//
//
public class GlobalQueueFinisher extends Thread
{
	private GlobalQueueHeader globalQueueHeader = null;
	private GlobalDriver			parent = null;
        private int                             inOrgOffset = -1;
        private int[][]                         outFields;
        private int[][]                         outXlate;
        private boolean                         acronyms = false;
        private int                             maxFields;
        private String                          lastFirstList = "";
	private FormatName			formatName = null;

        private StringBuffer                    compF1 = new StringBuffer(100);
        private StringBuffer                    compF2 = new StringBuffer(100);
	private StringBuffer                    copyCat =new StringBuffer(100);
	private StringBuffer                    linePat = new StringBuffer(10);
        private StringBuffer                    sb = new StringBuffer(100);;

        private String                          addrN;
        private String                          changeFlag;
        private String                          copyBuild;
        private String                          copyCity;
	private String				copyComp;
        private String                          copyNcr;
        private String                          copyPc;
        private String                          copyRegn;
	private String				copyRes;
        private String                          field1;
        private String                          field2;
        private String                          first;
        private String                          fixCup;
        private String                          getPost;
	private String				last;
        private String                          middle;
        private String                          pacr;
        private String                          parsedBox;
	private String				parsedStreet;
        private String                          quesrc;
        private String                          revGrp;
        private String                          subStreet;
        private String                          vacr;

        private int                             apInd;
        private int                             copyCo;
        private int                             copyCt;
        private int                             copyLen;
        private int                             copyPe;
        private int                             fixInd;
        private int                             numLines;


	private byte[]				copyB;


	//private static final int                COPY_ORG        = -2;
        private static final int                SET_ZERO        = -3;
        private static final int                SET_NINETYNINE  = -4;
        private static final int                SET_QUEST10     = -5;
        private static final int                SET_NAMECT      = -6;
        private static final int                SET_NAMETP      = -7;
        private static final int                SET_CATEGORY    = -8;
        private static final int                SET_REVGRP      = -9;
        private static final int                SET_PCMASK      = -10;
        private static final int                SET_ORIGIN      = -11;
        private static final int                SET_BOXIND      = -12;
        private static final int                SET_PRSCTRY     = -13;
        private static final int                SET_ISO         = -14;
        private static final int                STATE_CHG       = -15;
        private static final int                CITY_CHG        = -16;
        private static final int                STRT_CHG        = -17;
        private static final int                SUB_CHG         = -18;
        private static final int                BLD_CHG         = -19;
        private static final int                SET_WINKEY      = -20;
        private static final int                SET_STREETKEY   = -21;
        private static final int                BUS_CHG         = -22;
        private static final int                PC_CHG          = -23;
        private static final int                SET_GEOLVL      = -24;
        private static final int                FMT_NAME        = -25;
        private static final int                FMT_ADDRESS     = -26;
        private static final int                SET_ZEROORFIVE  = -27;
        private static final int                SET_POSTCODE_1  = -29;
        private static final int                SET_POSTCODE_2  = -30;
        private static final int                FMT_COMPANY     = -31;
        private static final int                FMT_CITY        = -32;
        private static final int                FMT_REGION      = -33;
        private static final int                FMT_ADDR1       = -34;
        private static final int                FMT_ADDR2       = -35;
        private static final int                FMT_ADDR3       = -36;
        private static final int                FMT_ADDR4       = -37;
        private static final int                FMT_GEOG1       = -38;
        private static final int                FMT_GEOG2       = -39;
        private static final int                FMT_GEOG3       = -40;
        private static final int                SET_FIRST       = -41;
        private static final int                SET_MIDDLE      = -42;
        private static final int                SET_LAST        = -43;
        private static final int                SET_NAME_CAT    = -44;
        private static final int                SET_LATITUDE    = -45;
        private static final int                SET_LONGITUDE   = -46;
        private static final int                SET_TCR         = -47;
        private static final int                PR_NAMENUM_01   = -100;
        private static final int                PR_NAMENUM_02   = -101;
        private static final int                PR_NMFORM_01    = -110;
        private static final int                PR_NMFORM_02    = -111;

        private static final String             quest   = "??????????";
        private static final int[]              ntlookup = { 3, 1, 2, 4 };


	private static final char[][]		transTable =
	{
	    { 0xB5, 0x75 },
	    { 0xC0, 0x41 },
	    { 0xC1, 0x41 },
	    { 0xC2, 0x41 },
	    { 0xC3, 0x41 },
	    { 0xC4, 0x41 },
	    { 0xC5, 0x41 },
	    { 0xC6, 0x41 },
	    { 0xC7, 0x43 },
	    { 0xC8, 0x45 },
	    { 0xC9, 0x45 },
	    { 0xCA, 0x45 },
	    { 0xCB, 0x45 },
	    { 0xCC, 0x49 },
	    { 0xCD, 0x49 },
	    { 0xCE, 0x49 },
	    { 0xCF, 0x49 },
	    { 0xD0, 0x44 },
	    { 0xD1, 0x4E },
	    { 0xD2, 0x4F },
	    { 0xD3, 0x4F },
	    { 0xD4, 0x4F },
	    { 0xD5, 0x4F },
	    { 0xD6, 0x4F },
	    { 0xD7, 0x58 },
	    { 0xD8, 0x4F },
	    { 0xD9, 0x55 },
	    { 0xDA, 0x55 },
	    { 0xDB, 0x55 },
	    { 0xDC, 0x55 },
	    { 0xDD, 0x59 },
	    { 0xDE, 0x50 },
	    { 0xDF, 0x53 },
	    { 0xE0, 0x61 },
	    { 0xE1, 0x61 },
	    { 0xE2, 0x61 },
	    { 0xE3, 0x61 },
	    { 0xE4, 0x61 },
	    { 0xE5, 0x61 },
	    { 0xE6, 0x61 },
	    { 0xE7, 0x63 },
	    { 0xE8, 0x65 },
	    { 0xE9, 0x65 },
	    { 0xEA, 0x65 },
	    { 0xEB, 0x65 },
	    { 0xEC, 0x69 },
	    { 0xED, 0x69 },
	    { 0xEE, 0x69 },
	    { 0xEF, 0x69 },
	    { 0xF0, 0x64 },
	    { 0xF1, 0x6E },
	    { 0xF2, 0x6F },
	    { 0xF3, 0x6F },
	    { 0xF4, 0x6F },
	    { 0xF5, 0x6F },
	    { 0xF6, 0x6F },
	    { 0xF7, 0x78 },
	    { 0xF8, 0x6F },
	    { 0xF9, 0x75 },
	    { 0xFA, 0x75 },
	    { 0xFB, 0x75 },
	    { 0xFC, 0x75 },
	    { 0xFD, 0x79 },
	    { 0xFE, 0x70 },
	    { 0xFF, 0x79 },
	};

//
// This translation and mapping table is used by the GlobalFinisher to map
// information output from the optima server into the specific locations
// of the output record.
//
	private static String[][]		oftLookupNameTable =
	{
	    {"pr_return", 		String.valueOf(SET_ZERO), "", "" },
	    {"pr_confidence", 		String.valueOf(SET_ZEROORFIVE),"","" },
	    {"pr_comprehension", 	String.valueOf(SET_NINETYNINE),"","" },
	    {"pr_orig_linepat", 	String.valueOf(SET_QUEST10),"", "" },
	    //{"pr_line_rules", 		"", "", "" },
	    {"pr_name_review_codes", 	"", "", String.valueOf(OFT.NCR) },
	    //{"pr_street_review_codes", 	"", "", "" },
	    {"pr_geog_review_codes", 	"", String.valueOf(OFT.ACR), "" },
	    {"pr_misc_review_codes", 	"", String.valueOf(OFT.Percent), "" },
	    {"pr_global_review_codes", 	String.valueOf(OFT.ACR), "", ""},
	    {"pr_number_of_names", 	"", "", String.valueOf(SET_NAMECT) },
	    {"pr_name_types", 	       "", "", String.valueOf(SET_NAMETP) },
	    {"pr_category", 	       "", String.valueOf(SET_CATEGORY), "" },
	    {"pr_name_category",       "", String.valueOf(SET_NAME_CAT), "" },
	    {"pr_rev_group", 	       "", "", String.valueOf(SET_REVGRP) },
	    {"pr_hse_nbr", 	       String.valueOf(OFT.Premise),"","" },
	    {"pr_hse_nbr_display",     String.valueOf(OFT.Premise),"","" },
	    //{"pr_hse_mask", 	       "", "", "" },
	    //{"pr_hse_type", 	       "", "", "" },
	    {"pr_st_tl", 	       String.valueOf(OFT.Street),"","" },
	    {"pr_st_tl_display",       String.valueOf(OFT.Street),"","" },
	    //{"pr_st_type1", "", "",  "" },
	    //{"pr_st_type1_display",  "", "", "" },
	    //{"pr_st_type2", "", "",  "" },
	    //{"pr_st_type2_display",  "", "", "" },
	    //{"pr_pr_st_dir", "", "",  "" },
	    //{"pr_pr_st_dir_display",  "", "", "" },
	    //{"pr_sc_st_dir", "", "",  "" },
	    //{"pr_sc_st_dir_display",  "", "", "" },
	    //{"pr_rte_name", "", "",  "" },
	    //{"pr_rte_name_display",  "", "", "" },
	    //{"pr_rte_nbr", "", "",  "" },
	    //{"pr_rte_mask", "", "",  "" },
	    //{"pr_rte_type", "", "",  "" },
	    //{"pr_box_name", "", "",  "" },
	    //{"pr_box_name_display",  "", "", "" },
	    {"pr_box_nbr",	      String.valueOf(OFT.POBox) ,"",  "" },
	    //{"pr_box_mask", "", "",  "" },
	    //{"pr_box_type", "", "",  "" },
	    {"pr_complex1_name",      String.valueOf(OFT.Building),"","" },
	    {"pr_complex1_name_display",String.valueOf(OFT.Building),"","" },
	    //{"pr_complex1_type", "", "",  "" },
	    //{"pr_complex1_type_display",  "", "", "" },
	    //{"pr_complex2_name", "", "",  "" },
	    //{"pr_complex2_name_display",  "", "", "" },
	    //{"pr_complex2_type", "", "",  "" },
	    //{"pr_complex2_type_display",  "", "", "" },
	    //{"pr_complex3_name", "", "",  "" },
	    //{"pr_complex3_name_display",  "", "", "" },
	    //{"pr_complex3_type", "", "",  "" },
	    //{"pr_complex3_type_display",  "", "", "" },
	    {"pr_dwel1_name",   String.valueOf(OFT.SubBuilding),  "", "" },
	    {"pr_dwel1_name_display",String.valueOf(OFT.SubBuilding),"","" },
	    //{"pr_dwel1_nbr", "", "",  "" },
	    //{"pr_dwel1_mask", "", "",  "" },
	    //{"pr_dwel1_type", "", "",  "" },
	    //{"pr_dwel2_name", "", "",  "" },
	    //{"pr_dwel2_name_display",  "", "", "" },
	    //{"pr_dwel2_nbr", "", "",  "" },
	    //{"pr_dwel2_mask", "", "",  "" },
	    //{"pr_dwel2_type", "", "",  "" },
	    //{"pr_dwel3_name", "", "",  "" },
	    //{"pr_dwel3_name_display",  "", "", "" },
	    //{"pr_dwel3_nbr", "", "",  "" },
	    //{"pr_dwel3_mask", "", "",  "" },
	    //{"pr_dwel3_type", "", "",  "" },
	    //{"pr_misc_addr", "", "",  "" },
	    {"pr_best_number", String.valueOf(OFT.Premise), "", "" },
	    {"pr_best_st_tl", String.valueOf(OFT.Street), "", "" },
	    {"pr_country_name",        String.valueOf(SET_PRSCTRY),"","" },
	    {"pr_country_name_display",String.valueOf(SET_PRSCTRY),"","" },
	    {"pr_neigh1_name",	    String.valueOf(OFT.SubCity),  "", "" },
	    {"pr_neigh1_name_display", String.valueOf(OFT.SubCity),  "", "" },
	    //{"pr_neigh2_name", "", "",  "" },
	    //{"pr_neigh2_name_display",  "", "", "" },
	    {"pr_city_name",           String.valueOf(OFT.City),  "", "" },
	    {"pr_city_name_display",   String.valueOf(OFT.City),  "", "" },
	    //{"pr_city_number", "", "",  "" },
	    //{"pr_city_status", "", "",  "" },
	    //{"pr_city_lname_dir", "",  "", "" },
	    {"pr_st_prov_cty_name", String.valueOf(OFT.Region),  "", "" },
	    {"pr_st_prov_cty_name_display",String.valueOf(OFT.Region),"","" },
	    {"pr_postal_code",     String.valueOf(SET_POSTCODE_1), "",  "" },
	    {"pr_postal_code_mask", String.valueOf(SET_PCMASK), "",  "" },
	    //{"pr_postal_code_type", "", "",  "" },
	    //{"pr_postal_code_dir", "", "",  "" },
	    //{"pr_postal_code_mask_dir", "",  "", "" },
	    //{"pr_postal_code_type_dir", "",  "", "" },
	    //{"pr_st_prov_number", "", "",  "" },
	    {"pr_world_origin", String.valueOf(SET_ORIGIN), "",  "" },

	    //{"pr_name_sect_01", 	 "", "", "" },
	    {"pr_name_number_01", "",  "", String.valueOf(PR_NAMENUM_01)},
	    {"pr_nmform_01", "", "",  String.valueOf(PR_NMFORM_01) },
	    {"pr_prefix_01", 		 "", "",String.valueOf(OFT.Title)},
	    {"pr_prefix_display_01", 	 "", "",String.valueOf(OFT.Title)},
	    {"pr_first_01", 	    "",  "",String.valueOf(SET_FIRST)},
	    {"pr_first_display_01", "",  "",String.valueOf(SET_FIRST)},
	    {"pr_middle1_01", "", "",  String.valueOf(SET_MIDDLE)},
	    {"pr_middle1_display_01", "","", String.valueOf(SET_MIDDLE)},
	    //{"pr_middle2_01", "", "",  "" },
	    //{"pr_middle2_display_01",  "", "", "" },
	    //{"pr_middle3_01", "", "",  "" },
	    //{"pr_middle3_display_01",  "", "", "" },
	    {"pr_last_01", 	"",  "",String.valueOf(SET_LAST)},
	    {"pr_last_display_01", "", "",String.valueOf(SET_LAST)},
	    {"pr_suffix_01", "",  "", String.valueOf(OFT.NameSuffix)},
	    {"pr_suffix_display_01", "","",String.valueOf(OFT.NameSuffix)},
	    //{"pr_gener_01", "", "",  "" },
	    //{"pr_gener_display_01",  "", "", "" },
	    {"pr_gender_01", "",  "", String.valueOf(OFT.Gender)},
	    //{"pr_busname_01", "",  "", "" },
	    //{"pr_busname_display_01",  "", "", "" },
	    //{"pr_connector_01", "", "",  "" },
	    //{"pr_connector_display_01",  "", "", "" },
	    //{"pr_relation_01", "", "",  "" },
	    //{"pr_relation_display_01",  "", "", "" },
	    //{"pr_orig_line_number_01",  "", "", "" },
	    //{"pr_name_category_01", "",  "", "" },

	    //{"pr_name_sect_02", "", "",  "" },
	    {"pr_name_number_02", "",  "", String.valueOf(PR_NAMENUM_02)},
	    {"pr_nmform_02", "", "",  String.valueOf(PR_NMFORM_02)},
	    //{"pr_prefix_02", "", "",  "" },
	    //{"pr_prefix_display_02",  "", "", "" },
	    //{"pr_first_02", "", "",  "" },
	    //{"pr_first_display_02",  "", "", "" },
	    //{"pr_middle1_02", "", "",  "" },
	    //{"pr_middle1_display_02",  "", "", "" },
	    //{"pr_middle2_02", "", "",  "" },
	    //{"pr_middle2_display_02",  "", "", "" },
	    //{"pr_middle3_02", "", "",  "" },
	    //{"pr_middle3_display_02",  "", "", "" },
	    //{"pr_last_02", "", "",  "" },
	    //{"pr_last_display_02",  "", "", "" },
	    //{"pr_suffix_02", "", "",  "" },
	    //{"pr_suffix_display_02",  "", "", "" },
	    //{"pr_gener_02", "", "",  "" },
	    //{"pr_gener_display_02",  "", "", "" },
	    //{"pr_gender_02", "", "",  "" },
	    {"pr_busname_02", "",  "", String.valueOf(OFT.Company)},
	    {"pr_busname_display_02", "","",String.valueOf(OFT.Company)},
	    //{"pr_connector_02", "",  "", "" },
	    //{"pr_connector_display_02",  "", "", "" },
	    //{"pr_relation_02", "", "",  "" },
	    //{"pr_relation_display_02",  "", "", "" },
	    //{"pr_orig_line_number_02",  "", "", "" },
	    //{"pr_name_category_02", "",  "", "" },

/*
	    {"pr_name_sect_03", "", "", "" },
	    {"pr_name_number_03", "", "", "" },
	    {"pr_nmform_03", "", "", "" },
	    {"pr_prefix_03", "", "", "" },
	    {"pr_prefix_display_01", "", "", "" },
	    {"pr_first_03", "", "", "" },
	    {"pr_first_display_03", "", "", "" },
	    {"pr_middle1_03", "", "", "" },
	    {"pr_middle1_display_03", "", "", "" },
	    {"pr_middle2_03", "", "", "" },
	    {"pr_middle2_display_03", "", "", "" },
	    {"pr_middle3_03", "", "", "" },
	    {"pr_middle3_display_03", "", "", "" },
	    {"pr_last_03", "", "", "" },
	    {"pr_last_display_03", "", "", "" },
	    {"pr_suffix_03", "", "", "" },
	    {"pr_suffix_display_03", "", "", "" },
	    {"pr_gener_03", "", "", "" },
	    {"pr_gener_display_03", "", "", "" },
	    {"pr_gender_03", "", "", "" },
	    {"pr_busname_03", "", "", "" },
	    {"pr_busname_display_03", "", "", "" },
	    {"pr_connector_03", "", "", "" },
	    {"pr_connector_display_03", "", "", "" },
	    {"pr_relation_03", "", "", "" },
	    {"pr_relation_display_03", "", "", "" },
	    {"pr_orig_line_number_03", "", "", "" },
	    {"pr_name_category_03", "", "", "" },

	    {"pr_name_sect_04", "", "", "" },
	    {"pr_name_number_04", "", "", "" },
	    {"pr_nmform_04", "", "", "" },
	    {"pr_prefix_04", "", "", "" },
	    {"pr_prefix_display_04", "", "", "" },
	    {"pr_first_04", "", "", "" },
	    {"pr_first_display_04", "", "", "" },
	    {"pr_middle1_04", "", "", "" },
	    {"pr_middle1_display_04", "", "", "" },
	    {"pr_middle2_04", "", "", "" },
	    {"pr_middle2_display_04", "", "", "" },
	    {"pr_middle3_04", "", "", "" },
	    {"pr_middle3_display_04", "", "", "" },
	    {"pr_last_04", "", "", "" },
	    {"pr_last_display_04", "", "", "" },
	    {"pr_suffix_04", "", "", "" },
	    {"pr_suffix_display_04", "", "", "" },
	    {"pr_gener_04", "", "", "" },
	    {"pr_gener_display_04", "", "", "" },
	    {"pr_gender_04", "", "", "" },
	    {"pr_busname_04", "", "", "" },
	    {"pr_busname_display_04", "", "", "" },
	    {"pr_connector_04", "", "", "" },
	    {"pr_connector_display_04", "", "", "" },
	    {"pr_relation_04", "", "", "" },
	    {"pr_relation_display_04", "", "", "" },
	    {"pr_orig_line_number_04", "", "", "" },
	    {"pr_name_category_04", "", "", "" },

	    {"pr_name_sect_05", "", "", "" },
	    {"pr_name_number_05", "", "", "" },
	    {"pr_nmform_05", "", "", "" },
	    {"pr_prefix_05", "", "", "" },
	    {"pr_prefix_display_05", "", "", "" },
	    {"pr_first_05", "", "", "" },
	    {"pr_first_display_05", "", "", "" },
	    {"pr_middle1_05", "", "", "" },
	    {"pr_middle1_display_05", "", "", "" },
	    {"pr_middle2_05", "", "", "" },
	    {"pr_middle2_display_05", "", "", "" },
	    {"pr_middle3_05", "", "", "" },
	    {"pr_middle3_display_05", "", "", "" },
	    {"pr_last_05", "", "", "" },
	    {"pr_last_display_05", "", "", "" },
	    {"pr_suffix_05", "", "", "" },
	    {"pr_suffix_display_05", "", "", "" },
	    {"pr_gener_05", "", "", "" },
	    {"pr_gener_display_05", "", "", "" },
	    {"pr_gender_05", "", "", "" },
	    {"pr_busname_05", "", "", "" },
	    {"pr_busname_display_05", "", "", "" },
	    {"pr_connector_05", "", "", "" },
	    {"pr_connector_display_05", "", "", "" },
	    {"pr_relation_05", "", "", "" },
	    {"pr_relation_display_05", "", "", "" },
	    {"pr_orig_line_number_05", "", "", "" },
	    {"pr_name_category_05", "", "", "" },

	    {"pr_name_sect_06", "", "", "" },
	    {"pr_name_number_06", "", "", "" },
	    {"pr_nmform_06", "", "", "" },
	    {"pr_prefix_06", "", "", "" },
	    {"pr_prefix_display_06", "", "", "" },
	    {"pr_first_06", "", "", "" },
	    {"pr_first_display_06", "", "", "" },
	    {"pr_middle1_06", "", "", "" },
	    {"pr_middle1_display_06", "", "", "" },
	    {"pr_middle2_06", "", "", "" },
	    {"pr_middle2_display_06", "", "", "" },
	    {"pr_middle3_06", "", "", "" },
	    {"pr_middle3_display_06", "", "", "" },
	    {"pr_last_06", "", "", "" },
	    {"pr_last_display_06", "", "", "" },
	    {"pr_suffix_06", "", "", "" },
	    {"pr_suffix_display_06", "", "", "" },
	    {"pr_gener_06", "", "", "" },
	    {"pr_gener_display_06", "", "", "" },
	    {"pr_gender_06", "", "", "" },
	    {"pr_busname_06", "", "", "" },
	    {"pr_busname_display_06", "", "", "" },
	    {"pr_connector_06", "", "", "" },
	    {"pr_connector_display_06", "", "", "" },
	    {"pr_relation_06", "", "", "" },
	    {"pr_relation_display_06", "", "", "" },
	    {"pr_orig_line_number_06", "", "", "" },
	    {"pr_name_category_06", "", "", "" },

	    {"pr_name_sect_07", "", "", "" },
	    {"pr_name_number_07", "", "", "" },
	    {"pr_nmform_07", "", "", "" },
	    {"pr_prefix_07", "", "", "" },
	    {"pr_prefix_display_07", "", "", "" },
	    {"pr_first_07", "", "", "" },
	    {"pr_first_display_07", "", "", "" },
	    {"pr_middle1_07", "", "", "" },
	    {"pr_middle1_display_07", "", "", "" },
	    {"pr_middle2_07", "", "", "" },
	    {"pr_middle2_display_07", "", "", "" },
	    {"pr_middle3_07", "", "", "" },
	    {"pr_middle3_display_07", "", "", "" },
	    {"pr_last_07", "", "", "" },
	    {"pr_last_display_07", "", "", "" },
	    {"pr_suffix_07", "", "", "" },
	    {"pr_suffix_display_07", "", "", "" },
	    {"pr_gener_07", "", "", "" },
	    {"pr_gener_display_07", "", "", "" },
	    {"pr_gender_07", "", "", "" },
	    {"pr_busname_07", "", "", "" },
	    {"pr_busname_display_07", "", "", "" },
	    {"pr_connector_07", "", "", "" },
	    {"pr_connector_display_07", "", "", "" },
	    {"pr_relation_07", "", "", "" },
	    {"pr_relation_display_07", "", "", "" },
	    {"pr_orig_line_number_07", "", "", "" },
	    {"pr_name_category_07", "", "", "" },

	    {"pr_name_sect_08", "", "", "" },
	    {"pr_name_number_08", "", "", "" },
	    {"pr_nmform_08", "", "", "" },
	    {"pr_prefix_08", "", "", "" },
	    {"pr_prefix_display_08", "", "", "" },
	    {"pr_first_08", "", "", "" },
	    {"pr_first_display_08", "", "", "" },
	    {"pr_middle1_08", "", "", "" },
	    {"pr_middle1_display_08", "", "", "" },
	    {"pr_middle2_08", "", "", "" },
	    {"pr_middle2_display_08", "", "", "" },
	    {"pr_middle3_08", "", "", "" },
	    {"pr_middle3_display_08", "", "", "" },
	    {"pr_last_08", "", "", "" },
	    {"pr_last_display_08", "", "", "" },
	    {"pr_suffix_08", "", "", "" },
	    {"pr_suffix_display_08", "", "", "" },
	    {"pr_gener_08", "", "", "" },
	    {"pr_gener_display_08", "", "", "" },
	    {"pr_gender_08", "", "", "" },
	    {"pr_busname_08", "", "", "" },
	    {"pr_busname_display_08", "", "", "" },
	    {"pr_connector_08", "", "", "" },
	    {"pr_connector_display_08", "", "", "" },
	    {"pr_relation_08", "", "", "" },
	    {"pr_relation_display_08", "", "", "" },
	    {"pr_orig_line_number_08", "", "", "" },
	    {"pr_name_category_08", "", "", "" },

	    {"pr_name_sect_09", "", "", "" },
	    {"pr_name_number_09", "", "", "" },
	    {"pr_nmform_09", "", "", "" },
	    {"pr_prefix_09", "", "", "" },
	    {"pr_prefix_display_09", "", "", "" },
	    {"pr_first_09", "", "", "" },
	    {"pr_first_display_09", "", "", "" },
	    {"pr_middle1_09", "", "", "" },
	    {"pr_middle1_display_09", "", "", "" },
	    {"pr_middle2_09", "", "", "" },
	    {"pr_middle2_display_09", "", "", "" },
	    {"pr_middle3_09", "", "", "" },
	    {"pr_middle3_display_09", "", "", "" },
	    {"pr_last_09", "", "", "" },
	    {"pr_last_display_09", "", "", "" },
	    {"pr_suffix_09", "", "", "" },
	    {"pr_suffix_display_09", "", "", "" },
	    {"pr_gener_09", "", "", "" },
	    {"pr_gener_display_09", "", "", "" },
	    {"pr_gender_09", "", "", "" },
	    {"pr_busname_09", "", "", "" },
	    {"pr_busname_display_09", "", "", "" },
	    {"pr_connector_09", "", "", "" },
	    {"pr_connector_display_09", "", "", "" },
	    {"pr_relation_09", "", "", "" },
	    {"pr_relation_display_09", "", "", "" },
	    {"pr_orig_line_number_09", "", "", "" },
	    {"pr_name_category_09", "", "", "" },

	    {"pr_name_sect_10", "", "", "" },
	    {"pr_name_number_10", "", "", "" },
	    {"pr_nmform_10", "", "", "" },
	    {"pr_prefix_10", "", "", "" },
	    {"pr_prefix_display_10", "", "", "" },
	    {"pr_first_10", "", "", "" },
	    {"pr_first_display_10", "", "", "" },
	    {"pr_middle1_10", "", "", "" },
	    {"pr_middle1_display_10", "", "", "" },
	    {"pr_middle2_10", "", "", "" },
	    {"pr_middle2_display_10", "", "", "" },
	    {"pr_middle3_10", "", "", "" },
	    {"pr_middle3_display_10", "", "", "" },
	    {"pr_last_10", "", "", "" },
	    {"pr_last_display_10", "", "", "" },
	    {"pr_suffix_10", "", "", "" },
	    {"pr_suffix_display_10", "", "", "" },
	    {"pr_gener_10", "", "", "" },
	    {"pr_gener_display_10", "", "", "" },
	    {"pr_gender_10", "", "", "" },
	    {"pr_busname_10", "", "", "" },
	    {"pr_busname_display_10", "", "", "" },
	    {"pr_connector_10", "", "", "" },
	    {"pr_connector_display_10", "", "", "" },
	    {"pr_relation_10", "", "", "" },
	    {"pr_relation_display_10", "", "", "" },
	    {"pr_orig_line_number_10", "", "", "" },
	    {"pr_name_category_10", "", "", "" },

	    {"pr_line_01", "", "", "" },
	    {"pr_line_02", "", "", "" },
	    {"pr_line_03", "", "", "" },
	    {"pr_line_04", "", "", "" },
	    {"pr_line_05", "", "", "" },
	    {"pr_line_06", "", "", "" },
	    {"pr_line_07", "", "", "" },
	    {"pr_line_08", "", "", "" },
	    {"pr_line_09", "", "", "" },
	    {"pr_line_10", "", "", "" },
*/

	    //{"pr_pattern", "", "",  "" },
	    {"pr_line_type", String.valueOf(SET_QUEST10), "",  "" },

	    {"tg_in_postal_code", String.valueOf(SET_POSTCODE_1),  "", "" },
	    {"tg_in_state", String.valueOf(OFT.Region), "",  "" },
	    {"tg_in_postal_city", String.valueOf(OFT.City),  "", "" },
	    {"tg_in_house_number", String.valueOf(OFT.Premise),  "", "" },
	    {"tg_in_street_name", String.valueOf(OFT.Street),  "", "" },
	    //{"tg_in_street_type", "", "",  "" },
	    //{"tg_in_street_dir", "", "",  "" },
	    {"tg_in_box_type", String.valueOf(OFT.POBox),  "", "" },
	    {"tg_in_box_number", String.valueOf(OFT.POBox),  "", "" },
	    {"tg_in_sub_building", String.valueOf(OFT.SubBuilding),"",""},
	    {"tg_in_sub_building_value",String.valueOf(OFT.SubBuilding),"",""},
	    //{"tg_in_sub_building2", "", "",  "" },
	    //{"tg_in_sub_building2_value", "",  "", "" },
	    {"tg_in_other1", String.valueOf(OFT.Principality),  "", "" },
	    //{"tg_in_other2", "", "",  "" },
	    {"tg_in_busname", String.valueOf(OFT.Company),"","" },
	    //{"tg_in_state_code", "", "",  "" },
	    //{"tg_in_city_code", "", "",  "" },
	    //{"tg_in_box_ind", "", "",  "" },
	    //{"tg_in_unit1_ind", "",  "", "" },
	    //{"tg_in_unit2_ind", "",  "", "" },
	    //{"tg_in_record_type", "",  "", "" },
	    {"tg_in_locality", String.valueOf(OFT.SubCity),  "", "" },
	    {"tg_in_substreet_name", String.valueOf(OFT.SubStreet),"",""},
	    //{"tg_in_substreet_type", "", "",  "" },
	    {"tg_in_building", String.valueOf(OFT.Building),  "", "" },
	    {"tg_in_cedex", String.valueOf(OFT.Cedex), "",  "" },
//
//
	    {"tg_out_ret_code", "", String.valueOf(SET_ZERO),  "" },
	    {"tg_out_postal_code", "", String.valueOf(SET_POSTCODE_2), "" },
	    {"tg_out_state", "", String.valueOf(OFT.Region), "" },
	    {"tg_out_postal_city", "", String.valueOf(OFT.City),  "" },
	    {"tg_out_house_number", "", String.valueOf(OFT.Premise), "" },
	    {"tg_out_street_name", "", String.valueOf(OFT.Street),  "" },
	    //{"tg_out_street_type", "", "",  "" },
	    //{"tg_out_street_dir", "", "",  "" },
	    {"tg_out_sub_building","",String.valueOf(OFT.SubBuilding),""},
	    {"tg_out_sub_building_value","",String.valueOf(OFT.SubBuilding),""},
	    //{"tg_out_sub_building2", "", "",  "" },
	    //{"tg_out_sub_building2_value", "",  "", "" },
	    //{"tg_out_other1", "", "",  "" },
	    //{"tg_out_other2", "", "",  "" },
	    {"tg_out_busname","",String.valueOf(OFT.Company),"" },
	    {"tg_out_box_ind", "", String.valueOf(SET_BOXIND),  "" },
	    //{"tg_out_unit1_ind", "", "",  "" },
	    //{"tg_out_unit2_ind", "", "",  "" },
	    {"tg_out_box_type", "", String.valueOf(OFT.POBox),  "" },
	    {"tg_out_box_number", "", String.valueOf(OFT.POBox),  "" },
	    //{"tg_out_rec_type", "", "",  "" },
	    {"tg_out_delivery_addr", "",  "", String.valueOf(FMT_ADDRESS)},
	    {"tg_out_province_changed", "", "", String.valueOf(STATE_CHG) },
	    {"tg_out_city_changed", "", String.valueOf(CITY_CHG),  "" },
	    {"tg_out_street_name_changed", "",  String.valueOf(STRT_CHG),"" },
	    //{"tg_out_street_type_changed", "",  "", "" },
	    //{"tg_out_street_direction_changed",  "", "", "" },
	    {"tg_out_dwelling_1_changed", "", String.valueOf(SUB_CHG),  "" },
	    {"tg_out_dwelling_2_changed", "", String.valueOf(BLD_CHG),  "" },
	    //{"tg_out_other1_changed", "", "",  "" },
	    //{"tg_out_other2_changed", "", "",  "" },
	    {"tg_out_business_name_changed", "", String.valueOf(BUS_CHG),"" },
	    {"tg_out_postal_code_changed", "", String.valueOf(PC_CHG), "" },
	    {"tg_out_fail_level", "", String.valueOf(SET_GEOLVL),  "" },
	    //{"tg_out_ext_key_1", "", "",  "" },
	    //{"tg_out_ext_key_2", "", "",  "" },
	    //{"tg_out_ext_key_3", "", "",  "" },
	    {"tg_out_locality", "", String.valueOf(OFT.SubCity),  "" },
	    {"tg_out_substreet_name", "", String.valueOf(OFT.SubStreet),""},
	    //{"tg_out_substreet_type", "", "",  "" },
	    {"tg_out_building", "", String.valueOf(OFT.Building),  "" },
	    {"tg_out_cedex", "", String.valueOf(OFT.Cedex),  "" },
	    {"tg_out_delivery_point", "", String.valueOf(OFT.DPS),  "" },
	    {"tg_out_postal_winkey", "", String.valueOf(SET_WINKEY),  "" },
	    {"tg_out_street_winkey", "", String.valueOf(SET_STREETKEY),  "" },
	    {"tg_out_country_iso3", "", String.valueOf(SET_ISO),  "" },
	    {"tg_out_salutation", "", "", String.valueOf(FMT_NAME)},
	    //{"tg_out_address_id", "", "",  "" },
//
//
	    {"meta_organisation_name","",String.valueOf(FMT_COMPANY), ""},
	    {"reconstructed_address_1","","",String.valueOf(FMT_ADDR1)},
	    {"reconstructed_address_2","","",String.valueOf(FMT_ADDR2)},
	    {"reconstructed_address_3","","",String.valueOf(FMT_ADDR3)},
	    {"reconstructed_address_4","","",String.valueOf(FMT_ADDR4)},
	    {"reconstructed_geog_1","","",String.valueOf(FMT_GEOG1)},
	    {"reconstructed_geog_2","","",String.valueOf(FMT_GEOG2)},
	    {"reconstructed_geog_3","","",String.valueOf(FMT_GEOG3)},
	    {"meta_recons_name","","",String.valueOf(OFT.FullName)},
	    {"meta_town_name","","",String.valueOf(FMT_CITY)},
	    {"meta_city_name","","",String.valueOf(FMT_CITY)},
	    {"meta_county_name","","",String.valueOf(FMT_REGION)},
	    {"meta_region_name","","",String.valueOf(FMT_REGION)},
	    {"meta_latitude","","",String.valueOf(SET_LATITUDE)},
	    {"meta_longitude","","",String.valueOf(SET_LONGITUDE)},
	    {"enhancement_flags","","",String.valueOf(SET_TCR)},
	};

//
// Converts parser field names into OFT enumeration/lookup codes
//
        public static int[] lookupOFTCode(String prFieldName)
        {

            for (int i = 0; i < oftLookupNameTable.length; i++)
            {
                if (oftLookupNameTable[i][0].equalsIgnoreCase(prFieldName))
                {
                    int[] ret = {-1, -1, -1};
                    for (int j = 0; j < ret.length; j++)
                    {
                        if (oftLookupNameTable[i][j+1].length() > 0)
                        {
                            ret[j] = Integer.parseInt(
                                                oftLookupNameTable[i][j+1]);
                        }
                    }
                    return ret;
                }
            }
            //System.err.println("Warning - field "+prFieldName+
                        //" was not located in translation table");
            return null;
        }
//
// the public constructor method for the GlobalFinisher is invoked by the
// globalDriver. the child is told who the driver is, the childs' thread id
// given a list of possible optima server host/ip addresses, output field
// offsets, lengths and data mappings. the same for the input oraddrl fields
// and finally a debug flag (set by the user in the parameter file)
//
// The constructor simply squirrels these away for later when it begins to
// process.
// for tracking, the current instance count of GlobalFinisher class is
// incremented
// so that it can be logged when instances actually appear or are garbage
// collected.
//
	public GlobalQueueFinisher(GlobalQueueHeader globalQueueHeader,
                               GlobalDriver parent, int maxFields,
                               int inOrgOffset, int[][] outFields, int[][] outXlate,
                               boolean acronyms, String lastFirstList)
	{
	    this.globalQueueHeader = globalQueueHeader;
	    this.parent = parent;
	    this.maxFields = maxFields;
	    this.inOrgOffset = inOrgOffset;
	    this.outFields = outFields;
	    this.outXlate = outXlate;
	    this.acronyms = acronyms;
	    this.lastFirstList = lastFirstList;
	    formatName = FormatName.getInstance(parent.getLogWriter(), true,
                                                                lastFirstList);
	}

//
// the 'run' method is a required interface method for a Thread. it is the
// threads' processing point and the thread remains inside the run method all
// the time it wishes to remain alive. (This doesn't mean it can't call
// other methods - it just mustn't return from here or it will die!
//
// The run method simply opens the server connection, registers that fact with
// the GlobalDriver and then sets to work getting input records from the
// Globaldriver, processing them through the optima server and returning the
// formatted output to the GlobalDriver for outputting.
// When the globalDriver has no more data, the loop breaks and the run method
// closes the server connection and returns - causing the death of the child.
//
// all requests for Parsing, validation and name/Address formatting are either
// performed inline or invoke deeper methods to perform the functionality.
// all optima server calls are headless timed in case they hang.
//
// All server calls have their return code checked by the 'testError' method -
// unfortunately the return codes are either 'good' or 'bad' and don't really
// defined why they should be bad. A certain amount of retry logic has been
// embedded here so that a failed 'parse' or 'validate' has multiple attempts
// at succeeding until deemed to have really failed (due to repeated failures).
//
	public void run()
	{
	    int numFinished = 0;
	    GlobalQueueEntity[] processQueue =
					globalQueueHeader.globalQueueEntity;

	    do
	    {
		numFinished = 0;
		for (int k = 0; k < processQueue.length; k++)
		{
		    if (processQueue[k] == null ||
			processQueue[k].recordStatus ==
						GlobalQueueEntity.FORMATTED ||
			processQueue[k].recordStatus ==
						GlobalQueueEntity.WRITTEN)
		    {
			numFinished++;
		    }
		    else if (processQueue[k].recordStatus ==
						GlobalQueueEntity.PARSED)
		    {
			for (int j = 0;
				j < processQueue[k].recordVector.size(); j++)
			{
			    GlobalRecord gr = (GlobalRecord)processQueue[k].
						recordVector.elementAt(j);
			    processQueue[k].sources[2].setField(OFT.FullName,
								gr.fullName);
       			    formatName.formatName(processQueue[k].sources[2]);
			    copyFieldsToOutput(processQueue[k], gr);
			}
			processQueue[k].recordStatus =
						GlobalQueueEntity.FORMATTED;
			numFinished++;
		    }
		}
		if (numFinished < processQueue.length)
		{
		    try
		    {
			Thread.sleep(2000);
		    }
		    catch (InterruptedException ie) {}
		}
	    }
	    while (numFinished < processQueue.length);

	    globalQueueHeader.finishState = GlobalQueueHeader.IS_FINISHED;
	    //System.err.println("GQF finished");
	}

//
// This method is used by the run loop to extract appropriate elements of
// parsed, validated and formatted data from the optima contact instances
// and place them into the appropriate output fields of the output record as
// mapped by the output data dictionary.
//
// Note that this extraction is driven by the output dictionary fields and not
// the contact contents simply because the output fields may not all be in the
// user supplied dictionary under all circumstances.
//
// Note also that, for each output field there is a choice of which data to
// take as determined by a giant switch statement driven by the
// OftLookupNameTable defined at the top of this class.
//
	private void copyFieldsToOutput(GlobalQueueEntity gqe,
						GlobalRecord globalRecord)
	{
	    //System.arraycopy(GlobalDriver.outRecordEmpty, 0,
					//data, 0, outRecord.length);

	    pacr = gqe.sources[0].getField(OFT.ACR);
	    parsedBox = gqe.sources[0].getField(OFT.POBox);
	    copyBuild = gqe.sources[0].getField(OFT.Building).trim();
	    subStreet = gqe.sources[0].getField(OFT.SubStreet);

	    linePat.setLength(0);
	    linePat.append("??????????");

	    if (pacr.length() == 21)
	    {
		if (pacr.charAt(4) == '0')
		{
		    if (gqe.sources[0].getField(OFT.Premise).length() > 0)
		    {
			pacr = pacr.substring(0,4) + "2"+ pacr.substring(5);
		    }
		    else if (parsedBox.length() > 0)
		    {
			pacr = pacr.substring(0,4) + "P"+ pacr.substring(5);
		    }
		    else if (copyBuild.length() > 0)
		    {
			pacr = pacr.substring(0,4) + "B"+ pacr.substring(5);
		    }
		}
		if (pacr.charAt(6) == '0')
		{
		    if (gqe.sources[0].getField(OFT.Street).length() > 0)
		    {
			pacr = pacr.substring(0,6) + "2"+ pacr.substring(7);
		    }
		    else if (subStreet.length() > 0)
		    {
			pacr = pacr.substring(0,6) + "s"+ pacr.substring(7);
		    }
		}
//
// Mod city return if blank (false city)
//
		if (pacr.charAt(10) != '0' &&
				gqe.sources[0].getField(OFT.City).trim().length() == 0)
		{
		    pacr = pacr.substring(0,10) + "0"+ pacr.substring(11);
		}
//
// Mod Region return if blank (false Region)
//
		if (pacr.charAt(12) != '0' &&
			gqe.sources[0].getField(OFT.Region).trim().length() == 0)
		{
		    pacr = pacr.substring(0,12) + "0"+ pacr.substring(13);
		}
	    }

	    vacr = gqe.sources[1].getField(OFT.ACR);

	    copyCat.setLength(0);
	    copyCat.append("x ------ ------");
	    copyCat.setCharAt(0, gqe.dqs);
	    if (pacr.length() >= 15)// && pacr.charAt(0) == 'C')
	    {
		copyCat.setCharAt(2, pacr.charAt(4));
		copyCat.setCharAt(3, pacr.charAt(6));
		copyCat.setCharAt(5, pacr.charAt(10));
		copyCat.setCharAt(6, pacr.charAt(12));
		copyCat.setCharAt(7, pacr.charAt(14));
		linePat.setCharAt(6, (pacr.charAt(10) > '0') ? 'G' : '?');
		//linePat.setCharAt(7, (pacr.charAt(12) > '0') ? 'G' : '?');
		linePat.setCharAt(8, (pacr.charAt(14) > '0') ? 'G' : '?');
	    }

	    if (gqe.sources[0] != gqe.sources[1] && vacr.length() >= 15)
	    {
		copyCat.setCharAt(9, vacr.charAt(4));
		copyCat.setCharAt(10, vacr.charAt(6));
		copyCat.setCharAt(12, vacr.charAt(10));
		copyCat.setCharAt(13, vacr.charAt(12));
		copyCat.setCharAt(14, vacr.charAt(14));
		if (vacr.charAt(10) == '4' || vacr.charAt(10) == '5' ||
			(vacr.charAt(10) == '2' && vacr.charAt(1) >= '2'))
		{
		    if (gqe.sources[0].getField(OFT.City).trim().equalsIgnoreCase(
		    			gqe.sources[1].getField(OFT.City).trim()))
		    {
			copyCat.setCharAt(12, '4');
		    }
		    else
		    {
			copyCat.setCharAt(12, '5');
		    }
		}
	    }

	    if (gqe.fixUp.length() > 0)
	    {
		copyCat.append(" "+gqe.fixUp);
	    }

	    if (gqe.errorCounter > 1)
	    {
		copyCat.append(" @");
	    }

	    if (gqe.countryStat.iso3.length() > 0)
	    {
		copyCat.append("|F00"+gqe.countryStat.iso3);
	    }
	    if (gqe.sources[0].getField(OFT.WCR).length() > 0)
	    {
		copyCat.append("|HOLD01");
	    }


	    first = gqe.sources[2].getField(OFT.FirstName);
	    middle = gqe.sources[2].getField(OFT.MiddleInitials);
	    last = gqe.sources[2].getField(OFT.LastName);

	    linePat.setCharAt(0, (last.length() > 0) ? 'N' : '?');

	    copyComp = gqe.sources[1].getField(OFT.Company);
	    if (copyComp.length() == 0 ||
		gqe.sources[0].getField(OFT.Company).equals(
					gqe.sources[1].getField(OFT.Company)))
	    {
		copyComp = gqe.sources[2].getField(OFT.Company);
		if (copyComp.length() == 0)
		{
		    copyComp = gqe.sources[1].getField(OFT.Company);
		}
		if (copyComp.length() == 0)
		{
		    copyComp = gqe.sources[0].getField(OFT.Company);
		}
	    }
	    linePat.setCharAt(1, (copyComp.length() > 0) ? 'F' : '?');
	    linePat.setCharAt(2, (copyBuild.length() > 0 ||
		gqe.sources[0].getField(OFT.SubBuilding).length() > 0) ? 'A' : '?');
	    linePat.setCharAt(3, (subStreet.length() > 0) ? 'Z' : '?');
	    parsedStreet = gqe.sources[0].getField(OFT.Street).trim();
	    linePat.setCharAt(4, (parsedStreet.length() > 0) ? 'S' : '?');
	    linePat.setCharAt(5, (parsedBox.length() > 0) ? 'B' : '?');

	    gqe.countryStat.numCountryRecords++;

//
// So, for each output field that is available, look through the OftLookup
// and determine from which GlobalContact instance (parse, validate or
// formatted) the data is to be derived.
//
	    int k;
	    for (int j = 0; j < maxFields; j++)
	    {
		for (k = 0; k < 3; k++)
		{
//
// If the xlate table indicates a positive number then the data is simply to
// be copied from the respective GlobalContact instance/field into the output
// record. Note that truncation may occur if the data does not fit.
//
		    if (outXlate[j][k] >= 0)
		    {
			if (outFields[j][0] >= inOrgOffset)
			{
			    System.arraycopy(GlobalDriver.outRecordEmpty, 0,
					globalRecord.data,
					outFields[j][0], outFields[j][1]);
			}

			copyRes = gqe.sources[k].getField(outXlate[j][k]);
			if (copyRes.length() > 0)
			{
			    copyB = copyRes.getBytes();
			    copyLen = (copyB.length < outFields[j][1]) ?
						copyB.length : outFields[j][1];
			    if (Conversion.fixUTF8(copyB, copyLen))
			    {
				parent.logTruncatedField(outFields[j][2],
									copyB);
			    }

			    System.arraycopy(copyB, 0, globalRecord.data,
						outFields[j][0], copyLen);
			}
		    }
//
// on the other hand, the switch case (if not -1) indicates that the target
// output field is not a straight copy - but is derived or composed from the
// Global information across one or more GlobalContact instances.
//
		    else if (outXlate[j][k] != -1)
		    {
			switch (outXlate[j][k])
			{
//
// instruction to copy ORG_RECORD - always done once
//
/*
			    case COPY_ORG: // Copy org_record
				System.arraycopy(globalRecord.data, inOrgOffset,
					globalRecord.data,
					outFields[j][0], outFields[j][1]);
				break;
*/

//
// Some fields are set to '0'
//
			    case SET_ZERO: // Set 1 byte to '0'
				globalRecord.data[outFields[j][0]] = (byte)'0';
				break;

//
// Some set to '99' (dummy comp levels).
//
			    case SET_NINETYNINE: // set '99'
				globalRecord.data[outFields[j][0]] = (byte)'9';
				globalRecord.data[outFields[j][0]+1] = (byte)'9';
				break;

//
// Some set to '00' or '05' (dummy conf) 00 - no geog - 05 was geog
//
			    case SET_ZEROORFIVE: // set '00' or'05'
				globalRecord.data[outFields[j][0]] = (byte)'0';
				globalRecord.data[outFields[j][0]+1] = (byte)'5';
				if (pacr.indexOf("A0T0R0Z0") > 0)
				{
				    globalRecord.data[outFields[j][0]+1] = (byte)'0';
				}
				if (gqe.dqs != '1')
				{
				    gqe.countryStat.numCountryFallback[0]++;
				    if (gqe.sources[0] == gqe.sources[1])
				    {
				       gqe.countryStat.numCountryFallback[1]++;
				    }
				}
				break;

//
// 10 question marks for pr_orig_linepat
//
			    case SET_QUEST10: // set '??????????''
				if (gqe.errorCounter < 3)
				{
				    System.arraycopy(linePat.toString().
								getBytes(), 0,
				    globalRecord.data, outFields[j][0], 10);
				}
				else
				{
				    pacr = (pacr.length() < 1) ? "?" : pacr;
				    vacr = (vacr.length() < 1) ? "?" : vacr;
				    quesrc = pacr.substring(0,1)+
						    vacr.substring(0,1)+
						    quest;
				    System.arraycopy(quesrc.getBytes(), 0,
					globalRecord.data, outFields[j][0], 10);
				}
				break;

//
// Sets name count according to what names optima identified.
//
			    case SET_NAMECT: // Count pers+bus
				copyCt = (globalRecord.fullName.length() > 0)
								? 1 : 0;
				copyCt += (gqe.sources[0].getField(OFT.Company).
							length() > 0) ? 1 : 0;
				globalRecord.data[outFields[j][0]] = (byte)'0';
				globalRecord.data[outFields[j][0]+1]=(byte)
							(copyCt + '0');
				break;

//
// sets personal/business/error/mixed name type
//
			    case SET_NAMETP: // Count pers+bus
				//copyPe =(gqe.sources[2].getField(OFT.FullName).
								//length() > 0)
				copyPe = (globalRecord.fullName.length() > 0)
								? 1 : 0;
				copyCo = (gqe.sources[0].getField(OFT.Company).
							length() > 0) ? 2 : 0;
				globalRecord.data[outFields[j][0]] = (byte)
						(ntlookup[copyPe+copyCo] +'0');
				break;
//
// Set first name field
//
			    case SET_FIRST: // First name/initial
				if (first.length() > 0)
				{
				    copyB = first.getBytes();
				    copyLen = (copyB.length < outFields[j][1])?
						copyB.length : outFields[j][1];
				    if (Conversion.fixUTF8(copyB, copyLen))
				    {
					parent.logTruncatedField(
						outFields[j][2], copyB);
				    }
				    System.arraycopy(copyB, 0, globalRecord.data,
						outFields[j][0], copyLen);
				}
				break;
//
// Set middle name field
//
			    case SET_MIDDLE: // middle initials
				if (middle.length() > 0)
				{
				    copyB = middle.getBytes();
				    copyLen = (copyB.length < outFields[j][1])?
						copyB.length : outFields[j][1];
				    if (Conversion.fixUTF8(copyB, copyLen))
				    {
					parent.logTruncatedField(
						outFields[j][2], copyB);
				    }
				    System.arraycopy(copyB, 0, globalRecord.data,
						outFields[j][0], copyLen);
				}
				break;

//
// Set last name field
//
			    case SET_LAST: // last name
				if (last.length() > 0)
				{
				    copyB = last.getBytes();
				    copyLen = (copyB.length < outFields[j][1])?
						copyB.length : outFields[j][1];
				    if (Conversion.fixUTF8(copyB, copyLen))
				    {
					parent.logTruncatedField(
						outFields[j][2], copyB);
				    }
				    System.arraycopy(copyB, 0, globalRecord.data,
						outFields[j][0], copyLen);
				}
				break;

//
// builds NCR code into pr_name_category field
//
			    case SET_NAME_CAT:// Set NCR
				copyNcr = gqe.sources[2].getField(OFT.NCR);
				if (copyNcr.length() > 0)
				{
				    copyB = copyNcr.getBytes();
				    copyLen = (copyB.length < outFields[j][1])?
						copyB.length : outFields[j][1];
				    if (Conversion.fixUTF8(copyB, copyLen))
				    {
					parent.logTruncatedField(
						outFields[j][2], copyB);
				    }
				    System.arraycopy(copyB, 0, globalRecord.data,
						outFields[j][0], copyLen);
				}
				break;

//
// builds country code into pr_category as per Trillium
//
			    case SET_CATEGORY:// Set F00 & VULG
				if (copyCat.length() > 0)
				{
				    copyB = copyCat.toString().getBytes();
				    copyLen = (copyB.length < outFields[j][1])?
						copyB.length : outFields[j][1];
				    System.arraycopy(copyB, 0, globalRecord.data,
						outFields[j][0], copyLen);
				}
				break;

//
// tries to identify the review group that the record belongs to. Some of the
// standard trillium ones can't currently be diagnosed as they're not supported
// by Optima.
//
			    case SET_REVGRP:// Set PR Rev group
				if (gqe.sources[0].getField(OFT.WCR).length() >
									0)
				{
				    revGrp = "003";
				    gqe.countryStat.parseGroupCounters[3]++;
				}
				else if (pacr.length() == 21 &&
							pacr.charAt(0) != 'U')
				{
				    if (gqe.sources[2].getField(OFT.Gender).
								equals("C"))
				    {
					revGrp = "009";
				       gqe.countryStat.parseGroupCounters[9]++;
				    }
				    else if (pacr.indexOf("A0T0R0Z0") > 0)
				    {
					revGrp = "007";
				       gqe.countryStat.parseGroupCounters[7]++;
				    }
				    else if (pacr.indexOf("T0R0") > 0 &&
					gqe.sources[0].getField(OFT.City).
					length() == 0 &&
					gqe.sources[0].getField(
					OFT.Region).length() == 0)
				    {
					revGrp = "014";
				       gqe.countryStat.parseGroupCounters[14]++;
				    }
				    else if (pacr.indexOf("0S0A") > 0)
				    {
					revGrp = "006";
				       gqe.countryStat.parseGroupCounters[6]++;
				    }
				    else if (gqe.sources[0].getField(
					OFT.Company).length() == 0 &&
					last.length() == 0)
				    {
					revGrp = "005";
				       gqe.countryStat.parseGroupCounters[5]++;
				    }
				    else if (parsedStreet.length() > 0 &&
							subStreet.length() > 0)
				    {
					revGrp = "020";
				      gqe.countryStat.parseGroupCounters[20]++;
				    }
				    else if (middle.indexOf(" ") > 0)
				    {
					revGrp = "010";
				      gqe.countryStat.parseGroupCounters[10]++;
				    }
				    else if (gqe.sources[0].getField(
					OFT.Company).length() > 0 &&
					last.length() > 0)
				    {
					revGrp = "002";
				       gqe.countryStat.parseGroupCounters[2]++;
				    }
				    else
				    {
					revGrp = "000";
				       gqe.countryStat.parseGroupCounters[0]++;
				    }
				}
				else
				{
				    revGrp = "021";
				    gqe.countryStat.parseGroupCounters[21]++;
				}
				copyB = revGrp.getBytes();
				copyLen = (copyB.length < outFields[j][1]) ?
						copyB.length : outFields[j][1];
				System.arraycopy(copyB, 0, globalRecord.data,
						outFields[j][0], copyLen);
				break;

//
// World origin as per Trillium - derived from country ISO code.
//
			    case SET_ORIGIN:
				if (gqe.countryStat.iso3.equals("USA"))
				{
				    globalRecord.data[outFields[j][0]] = (byte)'1';
				}
				else if (gqe.countryStat.iso3.equals("CAN"))
				{
				    globalRecord.data[outFields[j][0]] = (byte)'2';
				}
				else if (gqe.countryStat.iso3.equals("GBR"))
				{
				    globalRecord.data[outFields[j][0]] = (byte)'3';
				}
				else if (gqe.countryStat.iso3.equals("BRA"))
				{
				    globalRecord.data[outFields[j][0]] = (byte)'5';
				}
				else if (gqe.countryStat.iso3.equals("AUS"))
				{
				    globalRecord.data[outFields[j][0]] = (byte)'6';
				}
				else if (gqe.countryStat.iso3.equals("DEU"))
				{
				    globalRecord.data[outFields[j][0]] = (byte)'7';
				}
				else if (gqe.countryStat.iso3.length() > 0)
				{
				    globalRecord.data[outFields[j][0]] = (byte)'4';
				}
				else
				{
				    globalRecord.data[outFields[j][0]] = (byte)'0';
				}
				break;

//
// Pbox indicator set if po box info in record.
//
			    case SET_BOXIND:// Set P if Pobox
				if (gqe.sources[1].getField(OFT.POBox).length() >0)
				{
				    globalRecord.data[outFields[j][0]] = (byte)'P';
				}
				break;

//
// Copies out country or principality name
//
			    case SET_PRSCTRY: // Set Country/Prin
				copyRes = gqe.sources[0].getField(OFT.Country);
				if (copyRes.length() == 0)
				{
				    copyRes = gqe.sources[0].getField(OFT.Principality);
				}
				if (copyRes.length() > 0)
				{
				    copyB = copyRes.getBytes();
				    copyLen = (copyB.length < outFields[j][1]) ?
						copyB.length : outFields[j][1];
				    if (Conversion.fixUTF8(copyB, copyLen))
				    {
					parent.logTruncatedField(
						outFields[j][2], copyB);
				    }
				    System.arraycopy(copyB, 0, globalRecord.data,
						outFields[j][0], copyLen);
				}
				break;
//
// Copies out ISO 3 letter country code
//
			    case SET_ISO: // Set ISO
				String isoV = gqe.sources[1].getField(
							OFT.CountryISO);
				isoV = (isoV.length() == 0) ? "ROW" : isoV;
				if (isoV.length() > 0)
				{
				    copyB = isoV.getBytes();
				    copyLen = (copyB.length < outFields[j][1]) ?
						copyB.length : outFields[j][1];
				    if (Conversion.fixUTF8(copyB, copyLen))
				    {
					parent.logTruncatedField(
						outFields[j][2], copyB);
				    }
				    System.arraycopy(copyB, 0, globalRecord.data,
						outFields[j][0], copyLen);
				}
				break;

//
// Validation state change flags  - set if Optima changed certain fields.
//
			    case STATE_CHG:// Set Y if state chg
				field1 = gqe.sources[0].getField(OFT.Region).trim().
							toUpperCase();
				field2 = gqe.sources[1].getField(OFT.Region).trim().
							toUpperCase();
				changeFlag = testFieldChange(field1, field2);

				if (changeFlag.length() > 0)
				{
				    globalRecord.data[outFields[j][0]] =
						(byte)changeFlag.charAt(0);
				    gqe.countryStat.validChangeCounters[0]++;
				}
				break;

			    case CITY_CHG:// Set Y if city chg
				field1 = gqe.sources[0].getField(OFT.City).trim().
							toUpperCase();
				field2 = gqe.sources[1].getField(OFT.City).trim().
							toUpperCase();
				changeFlag = testFieldChange(field1, field2);

				if (changeFlag.length() > 0)
				{
				    globalRecord.data[outFields[j][0]] =
						(byte)changeFlag.charAt(0);
				    gqe.countryStat.validChangeCounters[1]++;
				}
				break;

			    case STRT_CHG:// Set Y if street chg
				field2 = gqe.sources[1].getField(OFT.Street).trim().
							toUpperCase();
				changeFlag = testFieldChange(
					parsedStreet.toUpperCase(), field2);

				if (changeFlag.length() > 0)
				{
				    globalRecord.data[outFields[j][0]] =
						(byte)changeFlag.charAt(0);
				    gqe.countryStat.validChangeCounters[2]++;
				}
				break;

			    case SUB_CHG:// Set Y if apt chg
				field1 =
				    gqe.sources[0].getField(OFT.SubBuilding).trim().
							toUpperCase();
				field2 =
				    gqe.sources[1].getField(OFT.SubBuilding).trim().
							toUpperCase();
				changeFlag = testFieldChange(field1, field2);

				if (changeFlag.length() > 0)
				{
				    globalRecord.data[outFields[j][0]] =
						(byte)changeFlag.charAt(0);
				    gqe.countryStat.validChangeCounters[3]++;
				}
				break;

			    case BLD_CHG:// Set Y if bldg chg
				field2 =
				    gqe.sources[1].getField(OFT.Building).trim().
							toUpperCase();
				changeFlag = testFieldChange(
					copyBuild.toUpperCase(), field2);

				if (changeFlag.length() > 0)
				{
				    globalRecord.data[outFields[j][0]] =
						(byte)changeFlag.charAt(0);
				    gqe.countryStat.validChangeCounters[4]++;
				}
				break;

			    case BUS_CHG:// Set Y if business chg
				field1 =gqe.sources[0].getField(OFT.Company).trim().
							toUpperCase();
				field2 =gqe.sources[1].getField(OFT.Company).trim().
							toUpperCase();
				changeFlag = testFieldChange(field1, field2);

				if (changeFlag.length() > 0)
				{
				    globalRecord.data[outFields[j][0]] =
						(byte)changeFlag.charAt(0);
				    gqe.countryStat.validChangeCounters[5]++;
				}
				break;

			    case PC_CHG:// Set Y if Postcode chg
				field1 = getPostcodeFrom(gqe.sources[0]).trim().
							toUpperCase();
				field2 = getPostcodeFrom(gqe.sources[1]).trim().
							toUpperCase();
				changeFlag = testFieldChange(field1, field2);

				if (changeFlag.length() > 0)
				{
				    globalRecord.data[outFields[j][0]] =
						(byte)changeFlag.charAt(0);
				    gqe.countryStat.validChangeCounters[6]++;
				}
				break;

//
// validation level and counting (basically, the higher the number the further
// down the country/city/street/premise table the validator went.
//
			    case SET_GEOLVL: // Geo ret code
				if (gqe.sources[0] != gqe.sources[1])
				{
				    gqe.countryStat.numCountryGeocoded++;
				    //numGeocoded++;
				    switch (vacr.charAt(1))
				    {
					case '0':
					    globalRecord.data[outFields[j][0]] =
								(byte)'1';
					    gqe.countryStat.
						validGroupCounters[0]++;
					    break;
					case '1':
					    if (pacr.charAt(10) < '2')
					    {
						globalRecord.data[outFields[j][0]] =
								(byte)'1';
						gqe.countryStat.
						      validGroupCounters[1]++;
						break;
					    }
					    // assume ok city if parsed
					case '2':
					    globalRecord.data[outFields[j][0]] =
								(byte)'2';
					    gqe.countryStat.
						validGroupCounters[2]++;
					    break;
					case '3':
					    globalRecord.data[outFields[j][0]] =
								(byte)'2';
					    gqe.countryStat.
						validGroupCounters[3]++;
					    break;
					case '4':
					    globalRecord.data[outFields[j][0]] =
								(byte)'5';
					    gqe.countryStat.
						validGroupCounters[4]++;
					    break;
					case '5':
					    globalRecord.data[outFields[j][0]] =
								(byte)'0';
					    gqe.countryStat.
						validGroupCounters[5]++;
					    break;

					default: // is already set to '0'
					    globalRecord.data[outFields[j][0]] =
								(byte)'0';
					    gqe.countryStat.
						validGroupCounters[0]++;
				    }
				}
				break;

//
// take Company into company output area
//
			    case FMT_COMPANY: // Format company
				System.arraycopy(GlobalDriver.outRecordEmpty,0,
					globalRecord.data,
					outFields[j][0], outFields[j][1]);
				if (copyComp.length() > 0)
				{
				    copyComp = fixupCompany(copyComp+ " ");
				    copyB = copyComp.getBytes();
				    copyLen = (copyB.length < outFields[j][1])?
						copyB.length : outFields[j][1];
				    if (Conversion.fixUTF8(copyB, copyLen))
				    {
					parent.logTruncatedField(
						outFields[j][2], copyB);
				    }
				    System.arraycopy(copyB, 0, globalRecord.data,
						outFields[j][0], copyLen);
				}
				break;
//
// Take formatted city name to meta_town
//
			    case FMT_CITY: // Format city
				copyCity = gqe.sources[2].getField(OFT.City);
				if (gqe.countryStat.iso3.equals("FRA"))
                                {
                                    String copyCedex =
                                        gqe.sources[2].getField(OFT.Cedex);
                                    if (copyCedex.length() > 0)
                                    {
                                        copyCity += " "+copyCedex;
                                    }
                                }
				System.arraycopy(GlobalDriver.outRecordEmpty,0,
					globalRecord.data,
					outFields[j][0], outFields[j][1]);
				if (copyCity.length() > 0)
				{
				    copyB = copyCity.getBytes();
				    copyLen = (copyB.length < outFields[j][1]) ?
						copyB.length : outFields[j][1];
				    if (Conversion.fixUTF8(copyB, copyLen))
				    {
					parent.logTruncatedField(
						outFields[j][2], copyB);
				    }
				    System.arraycopy(copyB, 0, globalRecord.data,
						outFields[j][0], copyLen);
				}
				break;
//
// Take formatted region name to meta_county
//
			    case FMT_REGION: // Format region
				copyRegn = gqe.sources[2].getField(OFT.Region);
				System.arraycopy(GlobalDriver.outRecordEmpty,0,
					globalRecord.data,
					outFields[j][0], outFields[j][1]);
				if (copyRegn.length() > 0)
				{
				    copyB = copyRegn.getBytes();
				    copyLen = (copyB.length < outFields[j][1]) ?
						copyB.length : outFields[j][1];
				    if (Conversion.fixUTF8(copyB, copyLen))
				    {
					parent.logTruncatedField(
						outFields[j][2], copyB);
				    }
				    System.arraycopy(copyB, 0, globalRecord.data,
						outFields[j][0], copyLen);
				}
				break;
//
// Take formatted address line 1 to reconstructed_address_1
//
			    case FMT_ADDR1: // Format address
				addrN = gqe.sources[2].getField(
							OFT.AddressLine1);
				System.arraycopy(GlobalDriver.outRecordEmpty,0,
					globalRecord.data,
					outFields[j][0], outFields[j][1]);
				if (addrN.length() > 0)
				{
				    addrN=fixupApostrophes(addrN);
				    copyB = addrN.getBytes();
				    copyLen = (copyB.length < outFields[j][1]) ?
						copyB.length : outFields[j][1];
				    if (Conversion.fixUTF8(copyB, copyLen))
				    {
					parent.logTruncatedField(
						outFields[j][2], copyB);
				    }
				    System.arraycopy(copyB, 0, globalRecord.data,
						outFields[j][0], copyLen);
				}
				break;
//
// Take formatted address line 2 to reconstructed_address_2
//
			    case FMT_ADDR2: // Format address
				addrN = gqe.sources[2].getField(
							OFT.AddressLine2);
				System.arraycopy(GlobalDriver.outRecordEmpty,0,
					globalRecord.data,
					outFields[j][0], outFields[j][1]);
				if (addrN.length() > 0)
				{
				    addrN=fixupApostrophes(addrN);
				    copyB = addrN.getBytes();
				    copyLen = (copyB.length < outFields[j][1]) ?
						copyB.length : outFields[j][1];
				    if (Conversion.fixUTF8(copyB, copyLen))
				    {
					parent.logTruncatedField(
						outFields[j][2], copyB);
				    }
				    System.arraycopy(copyB, 0, globalRecord.data,
						outFields[j][0], copyLen);
				}
				break;
//
// Take formatted address line 3 to reconstructed_address_3
//
			    case FMT_ADDR3: // Format address
				addrN = gqe.sources[2].getField(
							OFT.AddressLine3);
				System.arraycopy(GlobalDriver.outRecordEmpty,0,
					globalRecord.data,
					outFields[j][0], outFields[j][1]);
				if (addrN.length() > 0)
				{
				    addrN=fixupApostrophes(addrN);
				    copyB = addrN.getBytes();
				    copyLen = (copyB.length < outFields[j][1]) ?
						copyB.length : outFields[j][1];
				    if (Conversion.fixUTF8(copyB, copyLen))
				    {
					parent.logTruncatedField(
						outFields[j][2], copyB);
				    }
				    System.arraycopy(copyB, 0, globalRecord.data,
						outFields[j][0], copyLen);
				}
				break;
//
// Take formatted address line 4 to reconstructed_address_4
//
			    case FMT_ADDR4: // Format address
				addrN = gqe.sources[2].getField(
							OFT.AddressLine4);
				System.arraycopy(GlobalDriver.outRecordEmpty,0,
					globalRecord.data,
					outFields[j][0], outFields[j][1]);
				if (addrN.length() > 0)
				{
				    addrN=fixupApostrophes(addrN);
				    copyB = addrN.getBytes();
				    copyLen = (copyB.length < outFields[j][1])?
						copyB.length : outFields[j][1];
				    if (Conversion.fixUTF8(copyB, copyLen))
				    {
					parent.logTruncatedField(
						outFields[j][2], copyB);
				    }
				    System.arraycopy(copyB, 0, globalRecord.data,
						outFields[j][0], copyLen);
				}
				break;
//
// Take formatted address line 6 to reconstructed_geog_1
//
			    case FMT_GEOG1: // Format geography
				if (gqe.addressFormat == 0)
				{
				    addrN = gqe.sources[2].getField(
							OFT.AddressLine6);
				}
				else
				{
				    addrN = gqe.sources[2].getField(
							OFT.AddressLine5);
				}
				System.arraycopy(GlobalDriver.outRecordEmpty,0,
					globalRecord.data,
					outFields[j][0], outFields[j][1]);
				if (addrN.length() > 0)
				{
				    addrN=fixupApostrophes(addrN);
				    copyB = addrN.getBytes();
				    copyLen = (copyB.length < outFields[j][1])?
						copyB.length : outFields[j][1];
				    if (Conversion.fixUTF8(copyB, copyLen))
				    {
					parent.logTruncatedField(
						outFields[j][2], copyB);
				    }
				    System.arraycopy(copyB, 0, globalRecord.data,
						outFields[j][0], copyLen);
				}
				break;
//
// Take formatted address line 7 to reconstructed_geog_2
//
			    case FMT_GEOG2: // Format geography
				if (gqe.addressFormat == 0)
				{
				    addrN = gqe.sources[2].getField(
							OFT.AddressLine7);
				}
				else
				{
				    addrN = gqe.sources[2].getField(
							OFT.AddressLine6);
				}
				System.arraycopy(GlobalDriver.outRecordEmpty,0,
					globalRecord.data,
					outFields[j][0], outFields[j][1]);
				if (addrN.length() > 0)
				{
				    addrN=fixupApostrophes(addrN);
				    copyB = addrN.getBytes();
				    copyLen = (copyB.length < outFields[j][1])?
						copyB.length : outFields[j][1];
				    if (Conversion.fixUTF8(copyB, copyLen))
				    {
					parent.logTruncatedField(
						outFields[j][2], copyB);
				    }
				    System.arraycopy(copyB, 0, globalRecord.data,
						outFields[j][0], copyLen);
				}
				break;
//
// Take formatted address line 8 to reconstructed_geog_3
//
			    case FMT_GEOG3: // Format geography
				if (gqe.addressFormat == 0)
				{
				    addrN = gqe.sources[2].getField(
							OFT.AddressLine8);
				}
				else
				{
				    addrN = gqe.sources[2].getField(
							OFT.AddressLine7);
				}
				System.arraycopy(GlobalDriver.outRecordEmpty,0,
					globalRecord.data,
					outFields[j][0], outFields[j][1]);
				if (addrN.length() > 0)
				{
				    addrN=fixupApostrophes(addrN);
				    copyB = addrN.getBytes();
				    copyLen = (copyB.length < outFields[j][1])?
						copyB.length : outFields[j][1];
				    if (Conversion.fixUTF8(copyB, copyLen))
				    {
					parent.logTruncatedField(
						outFields[j][2], copyB);
				    }
				    System.arraycopy(copyB, 0, globalRecord.data,
						outFields[j][0], copyLen);
				}
				break;
//
// take Salutation into formatted name output area
//
			    case FMT_NAME: // Formatted name
				copyB = gqe.sources[2].getField(
						OFT.Salutation).getBytes();
				copyLen = (copyB.length < outFields[j][1]) ?
						copyB.length : outFields[j][1];
				if (Conversion.fixUTF8(copyB, copyLen))
				{
				    parent.logTruncatedField(
						outFields[j][2], copyB);
				}
				System.arraycopy(copyB, 0, globalRecord.data,
						outFields[j][0], copyLen);

				break;
//
// take Salutation and address into formatted name/address output area.
//
			    case FMT_ADDRESS: // Formatted address
				numLines = 0;

				sb.setLength(0);

				for (int l = OFT.AddressLine1;
						l <= OFT.AddressLine8; l++)
				{
				    addrN = gqe.sources[2].getField(l);
				    sb.append(addrN+";");
				    numLines += (addrN.length()>0) ? 1 : 0;
				}
				if (numLines >= 2)
				{
				    for (int ll = sb.length() - 1;
					ll >= 0 && sb.charAt(ll) == ';'; ll--)
				    {
					sb.setLength(ll);
				    }
				    copyB = sb.toString().getBytes();
				    copyLen = (copyB.length < outFields[j][1]) ?
						copyB.length : outFields[j][1];
				    if (Conversion.fixUTF8(copyB, copyLen))
				    {
					parent.logTruncatedField(
						outFields[j][2], copyB);
				    }
				    System.arraycopy(copyB, 0, globalRecord.data,
						outFields[j][0], copyLen);
				}

				break;

//
// Set nmform_1 as personal.
//
			    case PR_NMFORM_01: // Name Form
				globalRecord.data[outFields[j][0]] = (byte)'1';
				break;
//
// Set nmform_2 as business.
//
			    case PR_NMFORM_02: // Name Form
				globalRecord.data[outFields[j][0]] = (byte)'2';
				break;

//
// set name number 1 as '01'
//
			    case PR_NAMENUM_01: // Name Number 1
				globalRecord.data[outFields[j][0]] = (byte)'0';
				globalRecord.data[outFields[j][0]+1] = (byte)'1';
				break;

//
// set name number 2 as '02'
//
			    case PR_NAMENUM_02: // Name Number 2
				globalRecord.data[outFields[j][0]] = (byte)'0';
				globalRecord.data[outFields[j][0]+1] = (byte)'2';
				break;

//
// Postal winkey for window key generator
//
			    case SET_WINKEY://PC outbnd or 4/5 digs
				copyPc = gqe.sources[1].getField(OFT.Postcode);
				if (copyPc.length() == 0)
				{
				    copyPc = gqe.sources[0].getField(OFT.Postcode);
				}

				copyPc = copyPc.replace('-',' ');
				int ind = copyPc.indexOf(' ');

				if (gqe.countryStat.iso3.equals("AND") &&
					 	copyPc.startsWith("AD"))
				{
				    copyPc = copyPc.substring(2);
				}
				else if (gqe.countryStat.iso3.equals("AZE") &&
					 	copyPc.startsWith("AZ "))
				{
				    copyPc = copyPc.substring(3);
				}
				else if (gqe.countryStat.iso3.equals("LVA") &&
						copyPc.startsWith("LV "))
				{
				    copyPc = copyPc.substring(3);
				}
				else if (gqe.countryStat.iso3.equals("GBR") == false &&
					 gqe.countryStat.iso3.equals("POL") == false &&
							ind >= 0 && ind < 4)
				{
				    copyPc = copyPc.substring(0, ind)+
						copyPc.substring(ind+1);
				}
				if (copyPc.indexOf(" ") > 0)
				{
				    copyPc = copyPc.substring(0,
							copyPc.indexOf(" "));
				}

				copyB = copyPc.toString().getBytes();
				copyLen = (copyB.length < outFields[j][1]) ?
						copyB.length : outFields[j][1];
				if (Conversion.fixUTF8(copyB, copyLen))
				{
				    parent.logTruncatedField(
						outFields[j][2], copyB);
				}
				System.arraycopy(copyB, 0, globalRecord.data,
						outFields[j][0], copyLen);
				break;
//
// Street key for window key generator - with 8 bit to 7 bit char conversion
//
			    case SET_STREETKEY://TG street or pr_bs
				field1 = gqe.sources[1].getField(OFT.Street);
				if (field1.length() == 0)
				{
				    field1 = parsedStreet;
				}
				field1 = toSevenBit(field1);
				while (field1.length() > 0 &&
					Character.isLetter(field1.charAt(0)) ==
									false)
				{
				    field1 = field1.substring(1);
				}
				copyB = field1.toString().getBytes();
				copyLen = (copyB.length < outFields[j][1]) ?
						copyB.length : outFields[j][1];
				if (Conversion.fixUTF8(copyB, copyLen))
				{
				    parent.logTruncatedField(
						outFields[j][2], copyB);
				}
				System.arraycopy(copyB, 0, globalRecord.data,
						outFields[j][0], copyLen);
				break;
//
// Build and set postcode - US is special
//
			    case SET_POSTCODE_1: // combine pc + dps (if us)
				copyPc = getPostcodeFrom(gqe.sources[0]);
				copyB = copyPc.getBytes();
				copyLen = (copyB.length < outFields[j][1]) ?
						copyB.length : outFields[j][1];
				if (Conversion.fixUTF8(copyB, copyLen))
				{
				    parent.logTruncatedField(
						outFields[j][2], copyB);
				}
				System.arraycopy(copyB, 0, globalRecord.data,
						outFields[j][0], copyLen);
				break;

			    case SET_POSTCODE_2: // combine pc + dps (if us)
				copyPc = getPostcodeFrom(gqe.sources[2]);
				//copyPc = getPostcodeFrom(gqe.sources[1]);
				copyB = copyPc.getBytes();
				copyLen = (copyB.length < outFields[j][1]) ?
						copyB.length : outFields[j][1];
				if (Conversion.fixUTF8(copyB, copyLen))
				{
				    parent.logTruncatedField(
						outFields[j][2], copyB);
				}
				System.arraycopy(copyB, 0, globalRecord.data,
						outFields[j][0], copyLen);
				break;

//
// take Latitude from Other9
//
			    case SET_LATITUDE: // Lat of Lat/Long
				System.arraycopy(GlobalDriver.outRecordEmpty,0,
					globalRecord.data,
					outFields[j][0], outFields[j][1]);
				copyB = gqe.sources[2].getField(
						OFT.Other9).getBytes();
				copyLen = (copyB.length < outFields[j][1]) ?
						copyB.length : outFields[j][1];
				if (Conversion.fixUTF8(copyB, copyLen))
				{
				    parent.logTruncatedField(
						outFields[j][2], copyB);
				}
				System.arraycopy(copyB, 0, globalRecord.data,
						outFields[j][0], copyLen);

				break;
//
// take Latitude from Other10
//
			    case SET_LONGITUDE: // Long of Lat/Long
				System.arraycopy(GlobalDriver.outRecordEmpty,0,
					globalRecord.data,
					outFields[j][0], outFields[j][1]);
				copyB = gqe.sources[2].getField(
						OFT.Other10).getBytes();
				copyLen = (copyB.length < outFields[j][1]) ?
						copyB.length : outFields[j][1];
				if (Conversion.fixUTF8(copyB, copyLen))
				{
				    parent.logTruncatedField(
						outFields[j][2], copyB);
				}
				System.arraycopy(copyB, 0, globalRecord.data,
						outFields[j][0], copyLen);

				break;
//
// take Enhancement Flags from Other8
//
			    case SET_TCR: // Long of Lat/Long
				System.arraycopy(GlobalDriver.outRecordEmpty,0,
					globalRecord.data,
					outFields[j][0], outFields[j][1]);
				copyB = gqe.sources[2].getField(
						OFT.Other8).getBytes();
				copyLen = (copyB.length < outFields[j][1]) ?
						copyB.length : outFields[j][1];
				if (Conversion.fixUTF8(copyB, copyLen))
				{
				    parent.logTruncatedField(
						outFields[j][2], copyB);
				}
				System.arraycopy(copyB, 0, globalRecord.data,
						outFields[j][0], copyLen);

				break;
//
// any other setting has been added by idiot into oftLookupNameTable but hasn't
// added the switch code to cater for it!
//
			    default:
				System.err.println("GlobalQueueFinisher"+
					": Can't interpret Xlate value"+
					" of "+outXlate[j][k]);
				System.exit(12);
			}
		    }
		}
	    }
//
// Now report on data non-recognition from original address lines
//
	    if (pacr.length() == 21)
	    {
//
// zip/postcode in adrlines 8 - pc
//
		if (pacr.charAt(14) == '0' && gqe.addressLines[9] != null)
		{
		    addrN = "P <"+gqe.addressLines[9] + "> ";
		    gqe.countryStat.addUnparsedPhrase(3, addrN);
		}
//
// state in adrlines 8 - region
//
		else if (pacr.charAt(12) == '0' && gqe.addressLines[8] != null)
		{
		    addrN = "R <"+gqe.addressLines[8] + "> ";
		    gqe.countryStat.addUnparsedPhrase(2, addrN);
		}
//
// town in adrlines 7 - city
//
		else if (pacr.charAt(10) == '0' && gqe.addressLines[7] != null)
		{
		    addrN = "C <"+gqe.addressLines[7] + "> ";
		    gqe.countryStat.addUnparsedPhrase(1, addrN);
		}
//
// Premise/street in adrlines 3-6
//
		else if (pacr.charAt(4) == '0' && pacr.charAt(6) == '0')
		{
		    addrN = "S ";
		    for (int i = 3; i < 7; i++)
		    {
			if (gqe.addressLines[i] != null)
			{
			    addrN += "<"+gqe.addressLines[i] + "> ";
			}
		    }
		    if (addrN.length() > 2)
		    {
			gqe.countryStat.addUnparsedPhrase(0, addrN);
		    }
		}
	    }
	}
//
// Method to compare two fields and determine whether a change has taken place
// from the first to the second and return a code indicating the nature of the
// change. Codes are -
//			Empty String 	- no change
//			I		- field has been filled (was blank)
//			U		- field has changed (neither blank)
//			D		- field has become blank (wasn't orig)
//
	private String testFieldChange(String f1, String f2)
	{
	    if (f1.equals(f2))
	    {
		return "";
	    }
	    else if (f1.length() == 0 && f2.length() > 0)
	    {
		return "I";
	    }
	    else if (f1.length() > 0 && f2.length() == 0)
	    {
		return "D";
	    }
	    int j = -1;

	    compF1.setLength(0);
	    for (int i = 0; i < f1.length(); i++)
	    {
		if (Character.isLetterOrDigit(f1.charAt(i)))
		{
		    compF1.append(f1.charAt(i));
		}
	    }
	    compF2.setLength(0);
	    for (int i = 0; i < f2.length(); i++)
	    {
		if (Character.isLetterOrDigit(f2.charAt(i)))
		{
		    compF2.append(f2.charAt(i));
		}
	    }
	    if (compF1.toString().equals(compF2.toString()))
	    {
		return "";
	    }
//
// If new value starts with old value then appended update - minor 'u'
//
	    if (compF2.toString().startsWith(compF1.toString()))
	    {
		return "u";
	    }
	    else if (Conversion.getLevenshteinDistance(compF1.toString(),
						compF2.toString(), false) > 89)
	    {
		return "u";
	    }
	    return "U";
	}
//
// Obtain/build a postcode + poss dps from contact
//
	private String getPostcodeFrom(COptimaContact contact)
	{
	    getPost = contact.getField(OFT.Postcode);
	    if (getPost.length() > 0 &&
			(contact.getField(OFT.CountryISO).equals("USA") ||
			 contact.getField(OFT.CountryISO).equals("PRT")))
	    {
		String dps = contact.getField(OFT.DPS);
		//System.err.println("In getPostCodeFrom "+getPost+" - "+dps);
		if (dps.length() > 0)
		{
		    getPost += "-"+dps;
		}
	    }
	    return getPost;
	}
//
// Method to convert Iso-latin-1 8 bit characters to equivalent 7 bit ascii
//
	private String toSevenBit(String eightBit)
	{
	    for (int i = 0; i < transTable.length; i++)
	    {
		eightBit = eightBit.replace(transTable[i][0],transTable[i][1]);
	    }

	    return eightBit.toUpperCase();
	}
//
// Method to fixup company acronyms, L' and .com
//
	private String fixupCompany(String comp)
	{
	    if (acronyms && comp.length() > 1)
	    {
		fixInd = comp.indexOf(" ");
		if (fixInd < 0 || fixInd > 3)
		{
		    fixInd = comp.indexOf("-");
		}
		if (fixInd < 0 || fixInd > 3 && comp.length() > 4 &&
				Character.isLetter(comp.charAt(0)) &&
				comp.charAt(0) < 2048)
		{
		    for (int i = 1; i < 5; i++)
		    {
			if (comp.charAt(i) >= 2048)
			{
			    fixInd = i;
			    break;
			}
		    }
		}
		if (fixInd > 1 && fixInd < 4 && comp.charAt(fixInd-1) != '.')
		{
		    fixCup = comp.toUpperCase();
		    if (fixCup.startsWith("AIR ") == false &&
			fixCup.startsWith("AIR-") == false &&
		        fixCup.startsWith("ASS ") == false &&
		        fixCup.startsWith("ASS-") == false &&
		        fixCup.startsWith("CO OP ") == false &&
		        fixCup.startsWith("CO-OP ") == false &&
		        fixCup.startsWith("DE ") == false &&
		        fixCup.startsWith("DE-") == false &&
		        fixCup.startsWith("DI ") == false &&
		        fixCup.startsWith("DI-") == false &&
		        fixCup.startsWith("DIE ") == false &&
		        fixCup.startsWith("DIE-") == false &&
		        fixCup.startsWith("LA ")  == false &&
		        fixCup.startsWith("LA-")  == false &&
		        fixCup.startsWith("LE ")  == false &&
		        fixCup.startsWith("LE-")  == false &&
		        fixCup.startsWith("LES ") == false &&
		        fixCup.startsWith("LES-") == false &&
		        fixCup.startsWith("MAX ") == false &&
		        fixCup.startsWith("MAX-") == false &&
		        fixCup.startsWith("NEW ") == false &&
		        fixCup.startsWith("NEW-") == false &&
		        fixCup.startsWith("SAN ") == false &&
		        fixCup.startsWith("SAN-") == false &&
		        fixCup.startsWith("SOC ") == false &&
		        fixCup.startsWith("SOC-") == false &&
		        fixCup.startsWith("ST ") == false &&
		        fixCup.startsWith("ST-") == false &&
		        fixCup.startsWith("THE ") == false &&
		        fixCup.startsWith("THE-") == false &&
		        fixCup.startsWith("VAN ") == false &&
		        fixCup.startsWith("VAN-") == false &&
		        fixCup.startsWith(comp.substring(0, fixInd)) == false)
		    {
			comp = fixCup.substring(0, fixInd) +
							comp.substring(fixInd);
		    }
		}
		else if (fixInd > 1 && fixInd < 4 &&
						comp.charAt(fixInd-1) == '.')
		{
		    fixCup = comp.toUpperCase();
		    if (fixCup.startsWith(comp.substring(0, fixInd)) == true)
		    {
			String clow = comp.toLowerCase();
			comp = fixCup.substring(0,1) +
					clow.substring(1, fixInd) +
					comp.substring(fixInd);
		    }
		}
	    }
	    comp = fixupApostrophes(comp);
//
// Uppercase initial letter always
//
	    if (comp.length() > 0)
	    {
		comp = comp.substring(0,1).toUpperCase() + comp.substring(1);
	    }
	    return comp.trim();
	}

	private String fixupApostrophes(String comp)
	{
	    comp = fixupApostrophe(comp, "D' ");
	    comp = fixupApostrophe(comp, "O' ");
	    return fixupApostrophe(comp, "L' ");
	}

	private String fixupApostrophe(String comp, String apost)
	{
	    apInd = 0;
	    while (comp.toUpperCase().indexOf(apost, apInd) >= 0)
	    {
		apInd = comp.toUpperCase().indexOf(apost, apInd);
		if (comp.length() > apInd + apost.length() &&
			(apInd == 0 || comp.charAt(apInd - 1) == ' ' ||
					comp.charAt(apInd - 1) == '-'))
		{
		    comp = comp.substring(0, apInd + apost.length()-1) +
					comp.substring(apInd+apost.length());
		}
		apInd++;
	    }
	    return comp;
	}
}
