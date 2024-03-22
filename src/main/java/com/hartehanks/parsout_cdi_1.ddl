Dictionary is CMPLDICT
Filename is PARSOUT

Record is PARSOUT
  Type is FIXED
  Length is 14642

//REDEFINE
Field is tg_match_area
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 0
  Length is 1500

Field is tg_in_postal_code
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 0
  Length is 15

Field is tg_in_state
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 15
  Length is 30

Field is tg_in_postal_city
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 45
  Length is 30

Field is tg_in_house_number
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 75
  Length is 15

Field is tg_in_street_name
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 90
  Length is 120

Field is tg_in_street_type
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 210
  Length is 15

Field is tg_in_street_dir
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 225
  Length is 12

Field is tg_in_box_type
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 237
  Length is 20

Field is tg_in_box_number
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 257
  Length is 20

Field is tg_in_sub_building
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 277
  Length is 20

Field is tg_in_sub_building_value
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 297
  Length is 20

Field is tg_in_sub_building2
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 317
  Length is 20

Field is tg_in_sub_building2_value
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 337
  Length is 10

Field is tg_in_other1
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 347
  Length is 60

Field is tg_in_other2
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 407
  Length is 60

Field is tg_in_busname
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 467
  Length is 100

Field is tg_in_state_code
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 567
  Length is 5

Field is tg_in_city_code
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 572
  Length is 5

Field is tg_in_box_ind
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 577
  Length is 1

Field is tg_in_unit1_ind
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 578
  Length is 1

Field is tg_in_unit2_ind
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 579
  Length is 1

Field is tg_in_filler4
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 580
  Length is 1

Field is tg_in_record_type
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 581
  Length is 1

Field is tg_in_filler
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 582
  Length is 108

Field is tg_out_ret_code
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 690
  Length is 1

Field is tg_out_postal_code
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 691
  Length is 15

Field is tg_out_state
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 706
  Length is 30

//REDEFINE
Field is tg_out_postal_city_30
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 736
  Length is 30

Field is tg_out_postal_city
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 736
  Length is 50

//REDEFINE
Field is tg_out_house_number_15
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 786
  Length is 15

Field is tg_out_house_number
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 786
  Length is 50

Field is tg_out_street_name
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 836
  Length is 120

Field is tg_out_street_type
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 956
  Length is 15

Field is tg_out_street_dir
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 971
  Length is 12

Field is tg_out_sub_building
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 983
  Length is 20

Field is tg_out_sub_building_value
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 1003
  Length is 20

Field is tg_out_sub_building2
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 1023
  Length is 20

Field is tg_out_sub_building2_value
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 1043
  Length is 10

Field is tg_out_other1
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 1053
  Length is 80

Field is tg_out_other2
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 1133
  Length is 80

Field is tg_out_busname
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 1213
  Length is 100

Field is tg_out_box_ind
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 1313
  Length is 1

Field is tg_out_unit1_ind
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 1314
  Length is 1

Field is tg_out_unit2_ind
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 1315
  Length is 1

Field is tg_out_filler2
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 1316
  Length is 30

Field is tg_out_rec_type
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 1346
  Length is 1

Field is tg_out_delivery_addr
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 1347
  Length is 200

//REDEFINE
Field is tg_out_change_flags
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 1547
  Length is 11

Field is tg_out_province_changed
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 1547
  Length is 1

Field is tg_out_city_changed
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 1548
  Length is 1

Field is tg_out_street_name_changed
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 1549
  Length is 1

Field is tg_out_street_type_changed
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 1550
  Length is 1

Field is tgoutstreetdirectionchanged
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 1551
  Length is 1

Field is tg_out_dwelling_1_changed
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 1552
  Length is 1

Field is tg_out_dwelling_2_changed
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 1553
  Length is 1

Field is tg_out_other1_changed
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 1554
  Length is 1

Field is tg_out_other2_changed
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 1555
  Length is 1

Field is tg_out_business_name_changed
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 1556
  Length is 1

Field is tg_out_postal_code_changed
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 1557
  Length is 1

Field is tg_out_fail_level
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 1558
  Length is 1

Field is tg_out_ext_key_1
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 1559
  Length is 15

Field is tg_out_ext_key_2
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 1574
  Length is 15

Field is tg_out_ext_key_3
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 1589
  Length is 15

Field is tg_out_filler
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 1604
  Length is 126

Field is tg_in_dpnd_locality
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 1730
  Length is 30

Field is tg_in_locality
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 1760
  Length is 30

Field is tg_in_substreet_name
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 1790
  Length is 50

Field is tg_in_substreet_type
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 1840
  Length is 15

Field is tg_in_building
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 1855
  Length is 70

Field is tg_in_cedex
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 1925
  Length is 8

Field is tg_out_box_type
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 1933
  Length is 20

Field is tg_out_box_number
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 1953
  Length is 20

Field is tg_out_dpnd_locality
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 1973
  Length is 30

Field is tg_out_locality
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 2003
  Length is 30

Field is tg_out_substreet_name
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 2033
  Length is 50

Field is tg_out_substreet_type
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 2083
  Length is 15

Field is tg_out_building
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 2098
  Length is 80

Field is tg_out_cedex
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 2178
  Length is 7

Field is tg_out_delivery_point
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 2185
  Length is 2

Field is tg_out_address_id
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 2187
  Length is 17

Field is tg_out_postal_winkey
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 2204
  Length is 7

Field is tg_out_country_iso3
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 2211
  Length is 3

Field is tg_out_salutation
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 2214
  Length is 50

Field is tg_ext_filler
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 2264
  Length is 28

Field is pr_return
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 2292
  Length is 1

Field is pr_confidence
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 2293
  Length is 2

Field is pr_comprehension
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 2295
  Length is 2

Field is pr_orig_linepat
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 2297
  Length is 10

Field is pr_line_rules
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 2307
  Length is 20

Field is pr_name_review_codes
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 2327
  Length is 30

Field is pr_street_review_codes
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 2357
  Length is 30

Field is pr_geog_review_codes
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 2387
  Length is 30

Field is pr_misc_review_codes
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 2417
  Length is 30

Field is pr_global_review_codes
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 2447
  Length is 30

Field is pr_number_of_names
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 2477
  Length is 2

Field is pr_name_types
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 2479
  Length is 1

Field is pr_category
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 2480
  Length is 50

Field is pr_name_category
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 2530
  Length is 50

//REDEFINE
Field is pr_rev_group
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 2580
  Length is 3

Field is pr_rev_group_1
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 2580
  Length is 1

Field is pr_rev_group_2_3
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 2581
  Length is 2

Field is pr_hse_nbr
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 2583
  Length is 15

Field is pr_hse_nbr_display
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 2598
  Length is 15

Field is pr_hse_type
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 2613
  Length is 1

Field is pr_st_tl
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 2614
  Length is 120

Field is pr_st_tl_display
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 2734
  Length is 120

Field is pr_st_type1
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 2854
  Length is 15

Field is pr_st_type1_display
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 2869
  Length is 15

Field is pr_st_type2
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 2884
  Length is 15

Field is pr_st_type2_display
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 2899
  Length is 15

Field is pr_pr_st_dir
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 2914
  Length is 12

Field is pr_pr_st_dir_display
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 2926
  Length is 12

Field is pr_sc_st_dir
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 2938
  Length is 12

Field is pr_sc_st_dir_display
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 2950
  Length is 12

Field is pr_rte_name
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 2962
  Length is 20

Field is pr_rte_name_display
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 2982
  Length is 20

Field is pr_rte_nbr
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 3002
  Length is 8

Field is pr_rte_type
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 3010
  Length is 1

Field is pr_box_name
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 3011
  Length is 20

Field is pr_box_name_display
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 3031
  Length is 20

Field is pr_box_nbr
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 3051
  Length is 20

Field is pr_box_type
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 3071
  Length is 1

Field is pr_complex1_name
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 3072
  Length is 80

Field is pr_complex1_name_display
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 3152
  Length is 80

Field is pr_complex1_type
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 3232
  Length is 15

Field is pr_complex1_type_display
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 3247
  Length is 15

Field is pr_complex2_name
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 3262
  Length is 25

Field is pr_complex2_name_display
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 3287
  Length is 25

Field is pr_complex2_type
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 3312
  Length is 15

Field is pr_complex2_type_display
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 3327
  Length is 15

Field is pr_dwel1_name
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 3342
  Length is 20

Field is pr_dwel1_name_display
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 3362
  Length is 20

Field is pr_dwel1_nbr
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 3382
  Length is 10

Field is pr_dwel1_type
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 3392
  Length is 1

Field is pr_dwel2_name
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 3393
  Length is 20

Field is pr_dwel2_name_display
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 3413
  Length is 20

Field is pr_dwel2_nbr
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 3433
  Length is 10

Field is pr_dwel2_type
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 3443
  Length is 1

Field is pr_dwel3_name
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 3444
  Length is 20

Field is pr_dwel3_name_display
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 3464
  Length is 20

Field is pr_dwel3_nbr
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 3484
  Length is 10

Field is pr_dwel3_type
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 3494
  Length is 1

Field is pr_misc_addr
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 3495
  Length is 100

Field is pr_best_number
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 3595
  Length is 20

Field is pr_best_st_tl
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 3615
  Length is 120

Field is pr_country_name
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 3735
  Length is 40

Field is pr_country_name_display
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 3775
  Length is 40

Field is pr_neigh1_name
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 3815
  Length is 30

Field is pr_neigh2_name
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 3845
  Length is 30

Field is pr_city_name
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 3875
  Length is 30

Field is pr_city_name_display
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 3905
  Length is 30

Field is pr_city_number
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 3935
  Length is 6

Field is pr_city_lname_dir
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 3941
  Length is 30

Field is pr_st_prov_cty_name
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 3971
  Length is 30

Field is pr_st_prov_cty_name_display
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 4001
  Length is 30

Field is pr_postal_code
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 4031
  Length is 15

Field is pr_postal_code_type
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 4046
  Length is 1

Field is pr_postal_code_dir
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 4047
  Length is 15

Field is pr_st_prov_number
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 4062
  Length is 2

Field is pr_world_origin
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 4064
  Length is 1

Field is pr_post_office_code
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 4065
  Length is 6

//REDEFINE
Field is pr_name_sect_01
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 4071
  Length is 571

Field is pr_name_number_01
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 4071
  Length is 2

Field is pr_nmform_01
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 4073
  Length is 1

Field is pr_prefix_01
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 4074
  Length is 15

Field is pr_prefix_display_01
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 4089
  Length is 15

Field is pr_first_01
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 4104
  Length is 50

//REDEFINE
Field is pr_first_display_01
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 4154
  Length is 50

Field is pr_first_display_01_a
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 4154
  Length is 1

Field is pr_first_display_01_b
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 4155
  Length is 49

Field is pr_middle1_01
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 4204
  Length is 50

Field is pr_middle1_display_01
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 4254
  Length is 50

Field is pr_middle2_01
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 4304
  Length is 30

Field is pr_middle2_display_01
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 4334
  Length is 30

Field is pr_middle3_01
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 4364
  Length is 30

Field is pr_middle3_display_01
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 4394
  Length is 30

Field is pr_last_01
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 4424
  Length is 40

Field is pr_last_display_01
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 4464
  Length is 40

Field is pr_suffix_01
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 4504
  Length is 15

Field is pr_suffix_display_01
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 4519
  Length is 15

Field is pr_gener_01
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 4534
  Length is 10

Field is pr_gener_display_01
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 4544
  Length is 10

Field is pr_gender_01
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 4554
  Length is 1

Field is pr_busname_01
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 4555
  Length is 100

Field is pr_busname_display_01
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 4655
  Length is 100

Field is pr_connector_01
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 4755
  Length is 15

Field is pr_connector_display_01
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 4770
  Length is 15

Field is pr_relation_01
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 4785
  Length is 25

Field is pr_relation_display_01
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 4810
  Length is 25

Field is pr_orig_line_number_01
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 4835
  Length is 2

Field is pr_name_category_01
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 4837
  Length is 25

//REDEFINE
Field is pr_name_sect_02
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 4862
  Length is 571

//REDEFINE
Field is pr_name_sect_03
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 4862
  Length is 571

//REDEFINE
Field is pr_name_sect_04
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 4862
  Length is 571

//REDEFINE
Field is pr_name_sect_05
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 4862
  Length is 571

//REDEFINE
Field is pr_name_sect_06
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 4862
  Length is 571

//REDEFINE
Field is pr_name_sect_07
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 4862
  Length is 571

//REDEFINE
Field is pr_name_sect_08
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 4862
  Length is 571

//REDEFINE
Field is pr_name_sect_09
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 4862
  Length is 571

//REDEFINE
Field is pr_name_sect_10
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 4862
  Length is 571

Field is pr_name_number_02
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 4862
  Length is 2

Field is pr_nmform_02
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 4864
  Length is 1

Field is pr_prefix_02
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 4865
  Length is 15

Field is pr_prefix_display_02
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 4880
  Length is 15

Field is pr_first_02
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 4895
  Length is 15

Field is pr_first_display_02
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 4910
  Length is 15

Field is pr_middle1_02
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 4925
  Length is 15

Field is pr_middle1_display_02
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 4940
  Length is 15

Field is pr_middle2_02
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 4955
  Length is 15

Field is pr_middle2_display_02
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 4970
  Length is 15

Field is pr_middle3_02
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 4985
  Length is 15

Field is pr_middle3_display_02
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 5000
  Length is 15

Field is pr_last_02
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 5015
  Length is 30

Field is pr_last_display_02
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 5045
  Length is 30

Field is pr_suffix_02
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 5075
  Length is 15

Field is pr_suffix_display_02
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 5090
  Length is 15

Field is pr_gener_02
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 5105
  Length is 10

Field is pr_gener_display_02
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 5115
  Length is 10

Field is pr_gender_02
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 5125
  Length is 1

Field is pr_busname_02
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 5126
  Length is 100

Field is pr_busname_display_02
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 5226
  Length is 100

Field is pr_connector_02
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 5326
  Length is 15

Field is pr_connector_display_02
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 5341
  Length is 15

Field is pr_relation_02
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 5356
  Length is 25

Field is pr_relation_display_02
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 5381
  Length is 25

Field is pr_orig_line_number_02
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 5406
  Length is 2

Field is pr_name_category_02
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 5408
  Length is 25

Field is pr_line_type
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 5433
  Length is 10

//REDEFINE
Field is MAT_RECORD
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 5443
  Length is 184
  Default is SPACES

//REDEFINE
Field is matcher_common_data
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 5443
  Length is 108

Field is hhld_branch_number
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 5443
  Length is 4

Field is hhld_last_name
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 5447
  Length is 13

Field is hhld_street_name
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 5460
  Length is 15

Field is hhld_house_number
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 5475
  Length is 4

Field is hhld_postal_code
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 5479
  Length is 9

Field is hhld_number_members
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 5488
  Length is 8

Field is hhld_prime_flag
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 5496
  Length is 1

Field is indv_branch_number
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 5497
  Length is 4

Field is indv_first_name
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 5501
  Length is 5

Field is indv_postal_code
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 5506
  Length is 9

Field is indv_number_members
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 5515
  Length is 8

Field is indv_prime_flag
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 5523
  Length is 1

Field is susp_branch_number
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 5524
  Length is 4

Field is susp_first_name
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 5528
  Length is 5

Field is susp_postal_code
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 5533
  Length is 9

Field is susp_number_members
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 5542
  Length is 8

Field is susp_prime_flag
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 5550
  Length is 1

Field is window_number
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 5551
  Length is 8

Field is matched_hhld
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 5559
  Length is 8

Field is matched_indv_in_matched_hhld
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 5567
  Length is 8

Field is suspect_indv_in_matched_hhld
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 5575
  Length is 8

Field is suspect_hhld
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 5583
  Length is 8

Field is matched_indv_in_suspect_hhld
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 5591
  Length is 8

Field is suspect_indv_in_suspect_hhld
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 5599
  Length is 8

Field is record_number
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 5607
  Length is 8

Field is matched_hhld_pattern
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 5615
  Length is 3

Field is suspect_hhld_pattern
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 5618
  Length is 3

Field is matched_indv_pattern
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 5621
  Length is 3

Field is suspect_indv_pattern
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 5624
  Length is 3

//REDEFINE
Field is window_key_01
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 5627
  Length is 20
  Default is SPACES

Field is cc_candidate_code_01
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 5627
  Length is 20

//REDEFINE
Field is window_key_02
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 5647
  Length is 20
  Default is SPACES

Field is cc_candidate_code_02
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 5647
  Length is 20

//REDEFINE
Field is window_key_03
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 5667
  Length is 20
  Default is SPACES

Field is cc_candidate_code_03
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 5667
  Length is 20
  Default is SPACES

//REDEFINE
Field is ORG_RECORD
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 5687
  Length is 8955

Field is row_timestamp
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 5687
  Length is 19

Field is row_version
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 5706
  Length is 10

Field is soft_batch_id
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 5716
  Length is 10

Field is hard_batch_id
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 5726
  Length is 10

Field is unique_record_id
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 5736
  Length is 13
  Default is "                  0"

Field is unique_address_id
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 5749
  Length is 13
  Default is "                  0"

Field is current_window_key
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 5762
  Length is 20

Field is super_window_key
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 5782
  Length is 100

Field is trillium_matched_indiv_pattern
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 5882
  Length is 50

Field is current_household_key
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 5932
  Length is 13

Field is current_individual_key
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 5945
  Length is 13

Field is current_email_key
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 5958
  Length is 13

Field is trillium_current_indiv_key
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 5971
  Length is 13

Field is susp_household_key
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 5984
  Length is 13

Field is susp_indiv_in_matched_hhld_key
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 5997
  Length is 13

Field is matched_indiv_in_susp_hhld_key
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 6010
  Length is 13

Field is susp_indiv_in_susp_hhld_key
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 6023
  Length is 13

Field is previous_corporate_key
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 6036
  Length is 50

Field is previous_household_key
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 6086
  Length is 13

Field is previous_individual_key
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 6099
  Length is 13

Field is matched_household_pattern
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 6112
  Length is 50

Field is matched_individual_pattern
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 6162
  Length is 50

Field is suspect_household_pattern
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 6212
  Length is 50

Field is suspect_individual_pattern
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 6262
  Length is 50

Field is logically_deleted
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 6312
  Length is 1

Field is feed_mast_tran_code
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 6313
  Length is 1
  Default is "T"

Field is refresh_cycle_number
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 6314
  Length is 10

Field is incept_date_time
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 6324
  Length is 19

Field is meta_name_number
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 6343
  Length is 1

Field is meta_title
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 6344
  Length is 40

Field is meta_vocational_title
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 6384
  Length is 40

Field is meta_first_given_name
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 6424
  Length is 50

Field is meta_middle_names
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 6474
  Length is 50

Field is meta_surname
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 6524
  Length is 50

Field is meta_generation
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 6574
  Length is 15

Field is meta_honours
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 6589
  Length is 30

Field is meta_gender
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 6619
  Length is 1

Field is meta_recons_name
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 6620
  Length is 100

Field is meta_salutation
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 6720
  Length is 100

Field is meta_organisation_name
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 6820
  Length is 100

Field is reconstructed_address_1
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 6920
  Length is 120

Field is reconstructed_address_2
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 7040
  Length is 120

Field is reconstructed_address_3
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 7160
  Length is 120

Field is reconstructed_address_4
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 7280
  Length is 120

Field is reconstructed_geog_1
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 7400
  Length is 100

Field is reconstructed_geog_2
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 7500
  Length is 100

Field is reconstructed_geog_3
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 7600
  Length is 100

Field is meta_city_name
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 7700
  Length is 50

Field is meta_region_name
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 7750
  Length is 40

Field is meta_post_code
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 7790
  Length is 15

Field is meta_latitude
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 7805
  Length is 11

Field is meta_longitude
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 7816
  Length is 11

Field is meta_country_code_2
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 7827
  Length is 2

Field is meta_country_code_3
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 7829
  Length is 3

Field is meta_country_name
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 7832
  Length is 50

Field is name_quality_score
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 7882
  Length is 1

Field is address_quality_score
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 7883
  Length is 1

Field is parser_review_group
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 7884
  Length is 3

Field is parser_name_count
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 7887
  Length is 2

Field is parser_name_type
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 7889
  Length is 1

Field is parser_line_pattern
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 7890
  Length is 10

Field is name_category
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 7900
  Length is 50

Field is parser_category
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 7950
  Length is 50

Field is geocoder_return_code
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 8000
  Length is 1

Field is geocoder_change_flags
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 8001
  Length is 11

Field is Enhancement_flags
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 8012
  Length is 20

Field is match_fields
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 8032
  Length is 800

Field is review_flags
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 8832
  Length is 30

Field is Xxx_Suppressed
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 8862
  Length is 20

Field is last_edit_juldate
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 8882
  Length is 13

Field is meta_dummy2
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 8895
  Length is 50

Field is meta_dummy3
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 8945
  Length is 50

Field is meta_email
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 8995
  Length is 100

Field is valid_email_domain
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 9095
  Length is 100

Field is derived_product_key
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 9195
  Length is 150

Field is derived_site_key
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 9345
  Length is 100

Field is derived_contact_key
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 9445
  Length is 100

Field is consumer_product_key
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 9545
  Length is 100

Field is consumer_contact_key
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 9645
  Length is 50

Field is consumer_site_key
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 9695
  Length is 50

Field is source_id
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 9745
  Length is 50
  Default is "RGDM_UK"

Field is source_group
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 9795
  Length is 20
  Default is "RGDM"

Field is address1
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 9815
  Length is 120

Field is address2
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 9935
  Length is 120

Field is address3
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 10055
  Length is 120

Field is address4
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 10175
  Length is 120

Field is address5
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 10295
  Length is 120

Field is address6
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 10415
  Length is 120

Field is address7
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 10535
  Length is 120

Field is address8
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 10655
  Length is 120

Field is address9
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 10775
  Length is 120

Field is address10
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 10895
  Length is 120

Field is character_set
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 11015
  Length is 20

Field is routing_code
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 11035
  Length is 1
  Default is "C"

Field is product_category
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 11036
  Length is 1
  Default is 'C'

Field is record_expiry_date
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 11037
  Length is 19

Field is DUNS_NO
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 11056
  Length is 9

Field is Parent_DUNS_NO
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 11065
  Length is 9

Field is Domestic_Ultimate_DUNS_NO
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 11074
  Length is 9

Field is Global_Ultimate_DUNS_NO
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 11083
  Length is 9

Field is Contact_Phone
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 11092
  Length is 30

Field is Contact_Mobile_Phone
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 11122
  Length is 30

Field is Site_Phone
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 11152
  Length is 30

Field is Contact_email_address
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 11182
  Length is 75

Field is change_of_address_key
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 11257
  Length is 20

Field is change_of_address_regain_key
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 11277
  Length is 20

Field is Xxx_Suppression
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 11297
  Length is 1
  Default is 'U'

Field is mart_dummy1
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 11298
  Length is 200

Field is mart_dummy2
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 11498
  Length is 200

Field is mart_dummy3
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 11698
  Length is 200

Field is mart_dummy4
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 11898
  Length is 200

Field is mart_dummy5
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 12098
  Length is 200

Field is current_surname_key
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 12298
  Length is 13

Field is ext_return
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 12311
  Length is 1
  Default is "0"

//REDEFINE
Field is any_changes
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 12312
  Length is 6

Field is cand_changed
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 12312
  Length is 1
  Default is "N"

Field is super_cand_changed
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 12313
  Length is 1
  Default is "N"

Field is level1_key_changed
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 12314
  Length is 1
  Default is "N"

Field is level2_key_changed
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 12315
  Length is 1
  Default is "N"

Field is select_by_cc
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 12316
  Length is 1
  Default is "N"

Field is curr_prev_l1_key
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 12317
  Length is 13

Field is curr_prev_l2_key
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 12330
  Length is 13

Field is name_form
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 12343
  Length is 1

Field is temp_super_window_key
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 12344
  Length is 100

Field is Raw_Street_Name
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 12444
  Length is 50

Field is Raw_Street_Type
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 12494
  Length is 20

Field is Raw_SubStreet_Name
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 12514
  Length is 50

Field is Match_box_nbr
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 12564
  Length is 20

Field is Match_house_number
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 12584
  Length is 50

Field is Match_sub_building
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 12634
  Length is 15

Field is Match_building
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 12649
  Length is 40

Field is Match_complex2_name
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 12689
  Length is 25

Field is Match_complex2_type
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 12714
  Length is 15

Field is Match_postal_code
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 12729
  Length is 15

Field is Match_postal_city
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 12744
  Length is 30

Field is Match_business_name1
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 12774
  Length is 100

Field is Match_business_name2
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 12874
  Length is 100

//REDEFINE
Field is Match_pr_first_display_01
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 12974
  Length is 25

Field is Match_pr_first_display_01a
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 12974
  Length is 1

Field is Match_pr_first_display_01b
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 12975
  Length is 24

//REDEFINE
Field is Match_nickname
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 12999
  Length is 30

Field is Match_nickname_01a
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 12999
  Length is 1

Field is Match_nickname_01b
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 13000
  Length is 29

Field is Match_pr_middle_01
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 13029
  Length is 50

Field is Match_pr_last_01
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 13079
  Length is 40

Field is Match_country_code_2
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 13119
  Length is 2

Field is kept_matched_hhld
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 13121
  Length is 8

Field is kept_suspect_hhld
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 13129
  Length is 8

Field is passCode
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 13137
  Length is 3

Field is match_stream
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 13140
  Length is 2

Field is match_bad_company
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 13142
  Length is 100

Field is match_bad_street
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 13242
  Length is 100

Field is match_bad_city
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 13342
  Length is 100

Field is oraddrl1
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 13442
  Length is 120

Field is oraddrl2
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 13562
  Length is 120

Field is oraddrl3
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 13682
  Length is 120

Field is oraddrl4
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 13802
  Length is 120

Field is oraddrl5
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 13922
  Length is 120

Field is oraddrl6
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 14042
  Length is 120

Field is oraddrl7
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 14162
  Length is 120

Field is oraddrl8
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 14282
  Length is 120

Field is oraddrl9
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 14402
  Length is 120

Field is oraddrl10
  Type is ASCII CHARACTER
  Attributes are novalidation
  Starts in COLUMN 14522
  Length is 120


