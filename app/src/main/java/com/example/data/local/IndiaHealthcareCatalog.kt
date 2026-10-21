package com.example.data.local

import com.example.data.model.GovtHealthScheme
import com.example.data.model.HospitalFacility
import com.example.data.model.MedicineSafetyEntry
import com.example.data.model.OfflineHealthGuide
import com.example.data.model.PublicHealthAlert

object IndiaHealthcareCatalog {

    val hospitals = listOf(
        HospitalFacility(
            id = "hosp_aiims_bbsr",
            name = "AIIMS Bhubaneswar",
            stateAndDistrict = "Khordha, Odisha",
            facilityType = "AIIMS / Tertiary",
            distanceKm = 4.2,
            emergencyPhone = "06742476789",
            generalBedsAvailable = 48,
            generalBedsTotal = 420,
            icuBedsAvailable = 6,
            icuBedsTotal = 65,
            oxygenStatus = "Adequate (99% PSA Plant)",
            opdWaitMinutes = 45,
            ayushmanEmpanelled = true,
            specialties = listOf("24x7 Trauma", "General Medicine", "Maternal & Child", "Cardiology", "Jan Aushadhi"),
            isUnderservedZone = false,
            nearestReferralKm = 0
        ),
        HospitalFacility(
            id = "hosp_capital_bbsr",
            name = "Capital Hospital (Govt District HQ)",
            stateAndDistrict = "Unit-6, Bhubaneswar, Odisha",
            facilityType = "District Hospital",
            distanceKm = 2.8,
            emergencyPhone = "06742391983",
            generalBedsAvailable = 34,
            generalBedsTotal = 300,
            icuBedsAvailable = 4,
            icuBedsTotal = 28,
            oxygenStatus = "Adequate (96%)",
            opdWaitMinutes = 30,
            ayushmanEmpanelled = true,
            specialties = listOf("Emergency 24x7", "Free Diagnostics (Nidan)", "Free Medicines (Niramaya)", "Pediatrics"),
            isUnderservedZone = false,
            nearestReferralKm = 4
        ),
        HospitalFacility(
            id = "hosp_chc_tangi",
            name = "Community Health Centre (CHC) Tangi",
            stateAndDistrict = "Khordha Rural Block, Odisha",
            facilityType = "CHC (Rural Block)",
            distanceKm = 18.5,
            emergencyPhone = "108",
            generalBedsAvailable = 9,
            generalBedsTotal = 30,
            icuBedsAvailable = 0,
            icuBedsTotal = 0,
            oxygenStatus = "Moderate (82% Cylinders)",
            opdWaitMinutes = 15,
            ayushmanEmpanelled = true,
            specialties = listOf("24x7 Delivery Point", "Snakebite Anti-Venom", "Dengue/Malaria Rapid Test", "DOTS TB Care"),
            isUnderservedZone = true,
            nearestReferralKm = 32
        ),
        HospitalFacility(
            id = "hosp_phc_kalahandi",
            name = "Primary Health Centre (PHC-N) Thuamul Rampur",
            stateAndDistrict = "Kalahandi Tribal Belt, Odisha",
            facilityType = "PHC (Underserved Zone)",
            distanceKm = 27.0,
            emergencyPhone = "104",
            generalBedsAvailable = 4,
            generalBedsTotal = 6,
            icuBedsAvailable = 0,
            icuBedsTotal = 0,
            oxygenStatus = "Concentrators Only (72%)",
            opdWaitMinutes = 10,
            ayushmanEmpanelled = true,
            specialties = listOf("Immunization", "Antenatal Care", "ORS & Malaria Clinic", "eSanjeevani Tele-OPD"),
            isUnderservedZone = true,
            nearestReferralKm = 64
        ),
        HospitalFacility(
            id = "hosp_safdarjung_del",
            name = "Safdarjung Hospital & VMMC",
            stateAndDistrict = "New Delhi, Delhi NCR",
            facilityType = "Central Govt Tertiary",
            distanceKm = 6.1,
            emergencyPhone = "01126707444",
            generalBedsAvailable = 82,
            generalBedsTotal = 1500,
            icuBedsAvailable = 11,
            icuBedsTotal = 140,
            oxygenStatus = "Adequate (99% LMO)",
            opdWaitMinutes = 55,
            ayushmanEmpanelled = true,
            specialties = listOf("Burn & Trauma", "Emergency Medicine", "Obstetrics", "PM-JAY Desk"),
            isUnderservedZone = false,
            nearestReferralKm = 1
        ),
        HospitalFacility(
            id = "hosp_janaushadhi_kendra",
            name = "PM Bhartiya Janaushadhi Kendra #0412",
            stateAndDistrict = "Station Square, Cuttack/Bhubaneswar",
            facilityType = "Jan Aushadhi Pharmacy",
            distanceKm = 1.4,
            emergencyPhone = "18001808080",
            generalBedsAvailable = 0,
            generalBedsTotal = 0,
            icuBedsAvailable = 0,
            icuBedsTotal = 0,
            oxygenStatus = "N/A (Generic Medicines 50-90% Off)",
            opdWaitMinutes = 5,
            ayushmanEmpanelled = true,
            specialties = listOf("NABL Tested Generics", "Insulin & Cardiac Drugs", "Suvidha Sanitary Pads ₹1", "ORS & Surgical"),
            isUnderservedZone = false,
            nearestReferralKm = 2
        )
    )

    val governmentSchemes = listOf(
        GovtHealthScheme(
            id = "scheme_pmjay",
            name = "Ayushman Bharat PM-JAY",
            hindiName = "आयुष्मान भारत प्रधानमंत्री जन आरोग्य योजना",
            odiaName = "ଆୟୁଷ୍ମାନ ଭାରତ ପ୍ରଧାନମନ୍ତ୍ରୀ ଜନ ଆରୋଗ୍ୟ ଯୋଜନା",
            coverageAmount = "₹5,00,000 per family / year (plus ₹5L top-up for seniors 70+)",
            targetBeneficiaries = "SECC-listed low-income rural & urban families, ASHA/Anganwadi workers, and all senior citizens aged 70+",
            keyBenefits = listOf(
                "Cashless secondary & tertiary hospitalization across 29,000+ empanelled hospitals",
                "Covers 3 days pre-hospitalization and 15 days post-hospitalization medicines & diagnostics",
                "Zero cap on family size; all pre-existing diseases covered from Day 1"
            ),
            requiredDocuments = listOf(
                "Aadhaar Card or Ration Card (NFSA)",
                "PM-JAY Golden Card / Ayushman Card (can be generated at hospital Ayushman Mitra desk)",
                "Active mobile number for OTP verification"
            ),
            helpline = "14555",
            eligibilityCriteria = listOf("Ration Card Holder", "Rural / Low Income", "Senior Citizen (70+)")
        ),
        GovtHealthScheme(
            id = "scheme_bsky",
            name = "Gopabandhu Jan Arogya / BSKY (Odisha)",
            hindiName = "ओडिशा राज्य स्वास्थ्य आश्वासन योजना",
            odiaName = "ଗୋପବନ୍ଧୁ ଜନ ଆରୋଗ୍ୟ ଯୋଜନା / BSKY",
            coverageAmount = "₹5,00,000 per family + extra ₹5,00,000 for women members",
            targetBeneficiaries = "All National/State Food Security Act (NFSA/SFSS) ration card families in Odisha",
            keyBenefits = listOf(
                "100% free treatment & Niramaya medicines in all Odisha Government hospitals from Sub-Centre to Medical College",
                "Cashless critical care up to ₹10 Lakh for women members at empanelled private hospitals",
                "Free Nidan diagnostics (CT, MRI, Blood panels) at public hospitals"
            ),
            requiredDocuments = listOf(
                "Odisha Ration Card (NFSA / SFSS) or Health Smart Card",
                "Aadhaar Card of patient"
            ),
            helpline = "104",
            eligibilityCriteria = listOf("Odisha Resident", "Ration Card Holder", "Women & Maternal")
        ),
        GovtHealthScheme(
            id = "scheme_jsy",
            name = "Janani Suraksha Yojana (JSY) & JSSK",
            hindiName = "जननी सुरक्षा योजना एवं शिशु सुरक्षा कार्यक्रम",
            odiaName = "ଜନନୀ ସୁରକ୍ଷା ଯୋଜନା (JSY)",
            coverageAmount = "₹1,400 cash assistance (Rural) + 100% Free Delivery & C-Section",
            targetBeneficiaries = "Pregnant women delivering in government or accredited health institutions",
            keyBenefits = listOf(
                "Free institutional delivery including Caesarean section, drugs, and consumables",
                "Free 102/108 ambulance drop-home and pick-up from home to PHC/CHC",
                "Free treatment for sick newborns up to 1 year after birth"
            ),
            requiredDocuments = listOf(
                "MCP (Mother and Child Protection) Card from ASHA / ANM",
                "Aadhaar-seeded Bank Passbook for Direct Benefit Transfer (DBT)"
            ),
            helpline = "102",
            eligibilityCriteria = listOf("Women & Maternal", "Rural / Low Income")
        ),
        GovtHealthScheme(
            id = "scheme_pmbjp",
            name = "PM Bhartiya Janaushadhi Pariyojana (PMBJP)",
            hindiName = "प्रधानमंत्री भारतीय जनऔषधि परियोजना",
            odiaName = "ପ୍ରଧାନମନ୍ତ୍ରୀ ଭାରତୀୟ ଜନଔଷଧି ପରିଯୋଜନା",
            coverageAmount = "50% to 90% savings on NABL-certified Generic Medicines",
            targetBeneficiaries = "Open to every citizen without income or card restrictions",
            keyBenefits = listOf(
                "2,000+ WHO-GMP certified generic medicines and 300+ surgical items",
                "Diabetes, Hypertension, Cardiac, and Antibiotic courses at a fraction of branded MRP",
                "Ask your doctor to write the Generic Salt Name (e.g., Metformin, Amlodipine, Paracetamol)"
            ),
            requiredDocuments = listOf(
                "Valid Doctor Prescription for Schedule H / prescription medicines (No ID card required)"
            ),
            helpline = "18001808080",
            eligibilityCriteria = listOf("All Citizens", "Chronic Illness / Medicines", "Senior Citizen (70+)")
        )
    )

    val offlineGuides = listOf(
        OfflineHealthGuide(
            id = "guide_ors",
            titleEn = "Dehydration & Home ORS Preparation",
            titleHi = "निर्जलीकरण और घर पर ओआरएस (ORS) बनाने की विधि",
            titleOr = "ଡିହାଇଡ୍ରେସନ୍ ଏବଂ ଘରେ ORS ପ୍ରସ୍ତୁତି ପ୍ରଣାଳୀ",
            category = "First Aid & Child Care",
            redFlags = listOf(
                "Sunken eyes, dry tongue, or no urine for 6+ hours in children",
                "Blood in stool, persistent vomiting (unable to keep fluids down)",
                "Lethargy, confusion, or high fever > 102°F (38.9°C)"
            ),
            actionSteps = listOf(
                "Use a WHO-approved ORS packet mixed in 1 Litre of clean boiled & cooled water.",
                "If no packet is available: Mix 6 level teaspoons of sugar + 1/2 level teaspoon of salt in 1 Litre (5 cups) of clean drinking water.",
                "Give small, frequent sips every 5-10 minutes; continue breastfeeding for infants and start Zinc supplementation (for 14 days) as advised by ASHA/PHC."
            ),
            doNotDo = listOf(
                "Do NOT add extra salt (too much salt is dangerous for children).",
                "Do NOT give carbonated soft drinks or undiluted sugary fruit juices.",
                "Do NOT give anti-motility drugs to young children without a doctor's exam."
            ),
            sourceCitation = "WHO / MoHFW Integrated Management of Neonatal & Childhood Illness (IMNCI)"
        ),
        OfflineHealthGuide(
            id = "guide_fever_dengue",
            titleEn = "High Fever, Dengue & Malaria Warning Protocol",
            titleHi = "तेज बुखार, डेंगू और मलेरिया के खतरे के संकेत",
            titleOr = "ଉଚ୍ଚ ଜ୍ୱର, ଡେଙ୍ଗୁ ଏବଂ ମ୍ୟାଲେରିଆ ସତର୍କତା",
            category = "Infectious Disease Triage",
            redFlags = listOf(
                "Warning signs when fever starts dropping (Day 3-6): severe abdominal pain, persistent vomiting",
                "Bleeding from gums/nose, red pinpoint skin spots (petechiae), or black tarry stools",
                "Cold/clammy extremities, extreme restlessness, or sudden drop in blood pressure"
            ),
            actionSteps = listOf(
                "Use tepid (lukewarm) water sponging and plain Paracetamol per weight/adult guidelines for comfort.",
                "Drink plenty of oral fluids: ORS, coconut water, dal water, or lemon-salt water.",
                "Visit nearest Government PHC/CHC for free Malaria RDT and Dengue NS1/CBC platelet check within 24-48 hours."
            ),
            doNotDo = listOf(
                "NEVER take Aspirin, Ibuprofen, Diclofenac, or Nimesulide during suspected Dengue (increases bleeding risk).",
                "Do NOT take leftover antibiotics or steroid tablets without a clinical test."
            ),
            sourceCitation = "National Center for Vector Borne Diseases Control (NCVBDC), MoHFW India"
        ),
        OfflineHealthGuide(
            id = "guide_snakebite",
            titleEn = "Snakebite Golden-Hour Emergency Protocol",
            titleHi = "सांप के काटने पर आपातकालीन प्राथमिक उपचार",
            titleOr = "ସାପ କାମୁଡ଼ିলে ଜରୁରୀକାଳୀନ ପ୍ରାଥମିକ ଚିକିତ୍ସା",
            category = "Rural Emergency",
            redFlags = listOf(
                "Drooping eyelids (ptosis), difficulty swallowing or speaking, double vision (Neurotoxic)",
                "Rapid swelling of bitten limb, bleeding from gums or bite site (Hemotoxic)",
                "Breathing difficulty or drowsiness — call 108 immediately"
            ),
            actionSteps = listOf(
                "Keep the patient calm and completely still; immobilize the bitten limb with a splint like a fracture.",
                "Remove rings, bangles, anklets, or tight clothing near the limb before swelling starts.",
                "Rush directly to the nearest CHC or District Hospital equipped with Anti-Snake Venom (ASV) via 108 Ambulance."
            ),
            doNotDo = listOf(
                "Do NOT tie a tight tourniquet (can cause gangrene and limb loss).",
                "Do NOT cut, suck, burn, or apply herbal paste/black stones to the bite wound.",
                "Do NOT waste the Golden Hour visiting traditional healers (jhad-phook/ojha)."
            ),
            sourceCitation = "Standard Treatment Guidelines for Snakebite Management, MoHFW Govt of India"
        ),
        OfflineHealthGuide(
            id = "guide_maternal",
            titleEn = "Pregnancy & Postpartum Danger Signs",
            titleHi = "गर्भावस्था और प्रसव के बाद खतरे के लक्षण",
            titleOr = "ଗର୍ଭାବସ୍ଥା ଏବଂ ପ୍ରସବ ପରବର୍ତ୍ତୀ ବିପଦ ଲକ୍ଷଣ",
            category = "Maternal Health",
            redFlags = listOf(
                "Any vaginal bleeding during pregnancy or heavy bleeding after delivery",
                "Severe headache with blurred vision, swelling of face/hands, or convulsions (Eclampsia)",
                "Reduced or absent baby movements after 28 weeks, or high fever with foul discharge"
            ),
            actionSteps = listOf(
                "Call 102 (Janani Express) or 108 immediately for free transport to the nearest First Referral Unit (FRU/CHC).",
                "Keep your MCP Card, blood group report, and BSKY/Ayushman card ready in an emergency bag.",
                "Contact your village ASHA worker immediately so she can accompany you to the hospital."
            ),
            doNotDo = listOf(
                "Do NOT delay hospital transfer waiting for labor pains to subside at home if danger signs appear.",
                "Do NOT stop Iron-Folic Acid (IFA) or Calcium tablets without consulting your ANM/doctor."
            ),
            sourceCitation = "LaQshya & Surakshit Matritva Aashwasan (SUMAN), MoHFW India"
        )
    )

    val medicineSafetyList = listOf(
        MedicineSafetyEntry(
            id = "med_paracetamol",
            genericName = "Paracetamol (Acetaminophen) 500mg / 650mg",
            commonBrandsIndia = "Calpol, Crocin, Dolo, Pacimol",
            janAushadhiPriceApprox = "₹9 for 15 tablets (vs ₹30-₹35 branded)",
            primaryUse = "Symptomatic relief of fever and mild-to-moderate pain (headache, body ache).",
            dosageGuidanceInfo = "Educational reference: Adults typically space doses by at least 4–6 hours; maximum daily limit from all sources must never exceed 4,000 mg (or lower in liver disease).",
            keyWarnings = listOf(
                "Check combination cold/flu tablets — many already contain Paracetamol, risking accidental overdose.",
                "Overdose can cause severe, life-threatening liver damage.",
                "Safe first-line antipyretic in suspected Dengue fever (unlike NSAIDs)."
            ),
            interactions = "Avoid alcohol; consult doctor if taking Warfarin or anti-tubercular (ATT) medicines.",
            pregnancySafety = "Generally considered safe in pregnancy at standard lowest effective doses when clinically needed; verify with ANM/Doctor.",
            sourceCitation = "National List of Essential Medicines (NLEM) & Indian Pharmacopoeia Commission"
        ),
        MedicineSafetyEntry(
            id = "med_ors_zinc",
            genericName = "WHO Low-Osmolarity ORS + Zinc Sulfate 20mg",
            commonBrandsIndia = "Electral, ORSL-WHO, Walyte + Zinconia",
            janAushadhiPriceApprox = "₹4 per ORS sachet (Free at all Govt PHCs/ASHA)",
            primaryUse = "Prevention and treatment of dehydration from acute diarrhea or heat exhaustion.",
            dosageGuidanceInfo = "Dissolve entire 20.5g packet in exactly 1 Litre of clean drinking water. Discard unused solution after 24 hours.",
            keyWarnings = listOf(
                "Check for the 'WHO Formula (245 mOsm/L)' label — avoid high-sugar fruit beverages sold in tetra-packs for diarrhea.",
                "Do not boil the ORS solution after mixing the packet."
            ),
            interactions = "Safe with routine medications; space apart from certain antibiotics if advised.",
            pregnancySafety = "Completely safe and essential during pregnancy and breastfeeding for hydration.",
            sourceCitation = "WHO / MoHFW Intensified Diarrhoea Control Fortnight (IDCF) Guidelines"
        ),
        MedicineSafetyEntry(
            id = "med_metformin",
            genericName = "Metformin Hydrochloride 500mg",
            commonBrandsIndia = "Glycomet, Glucophage, Okamet",
            janAushadhiPriceApprox = "₹16 for 20 tablets (Free at Govt NCD Clinics)",
            primaryUse = "First-line prescription medicine for Type 2 Diabetes Mellitus.",
            dosageGuidanceInfo = "Prescription only. Usually taken with or immediately after meals to minimize stomach upset.",
            keyWarnings = listOf(
                "Do NOT start, stop, or change dosage without a doctor's blood sugar (FBS/PPBS/HbA1c) review.",
                "Hold temporarily before contrast CT scans or during severe dehydration/kidney illness as advised by a physician."
            ),
            interactions = "Excess alcohol increases risk of lactic acidosis; inform doctor of blood pressure or kidney medicines.",
            pregnancySafety = "Requires specialist obstetric review during pregnancy.",
            sourceCitation = "ICMR Guidelines for Management of Type 2 Diabetes"
        ),
        MedicineSafetyEntry(
            id = "med_amoxicillin",
            genericName = "Amoxicillin 500mg (Antibiotic - Red Line)",
            commonBrandsIndia = "Mox, Novamox, Cipmox",
            janAushadhiPriceApprox = "₹42 for 10 capsules (Schedule H Prescription Only)",
            primaryUse = "Confirmed bacterial infections only (e.g., bacterial pneumonia, strep throat, ear/dental infection).",
            dosageGuidanceInfo = "STRICT RED-LINE RULE: Never buy over-the-counter without a doctor's prescription. Always complete the full prescribed course.",
            keyWarnings = listOf(
                "Does NOT cure viral colds, seasonal flu, or Dengue fever.",
                "Severe allergy risk in patients with Penicillin hypersensitivity (rash, swelling, breathing trouble).",
                "Stopping mid-course drives antimicrobial resistance (AMR) in your community."
            ),
            interactions = "Inform doctor if taking blood thinners, methotrexate, or allopurinol.",
            pregnancySafety = "Used when prescribed by a doctor for bacterial infections in pregnancy.",
            sourceCitation = "ICMR Antimicrobial Stewardship & MoHFW Red-Line Campaign"
        )
    )

    val publicHealthAlerts = listOf(
        PublicHealthAlert(
            id = "alert_vector",
            region = "Odisha, Bihar, West Bengal & Coastal Districts",
            title = "Post-Monsoon Dengue & Malaria Surveillance Advisory",
            alertLevel = "ACTION_NEEDED",
            summary = "Increased Aedes mosquito breeding reported in stagnant water containers. Free NS1 & Malaria RDT testing active at all CHCs and Urban PHCs.",
            preventionChecklist = listOf(
                "Empty cooler trays, flower pots, and coconut shells every Sunday ('Dry Day')",
                "Use LLIN mosquito nets for children and pregnant women",
                "Seek free blood testing at your nearest PHC if fever lasts > 24 hours; avoid self-medicating with painkillers"
            ),
            communityResourceContact = "ASHA Worker / Helpline 104",
            source = "NCVBDC & State Public Health Directorate"
        ),
        PublicHealthAlert(
            id = "alert_anemia",
            region = "Pan-India Rural & Tribal Blocks (Anemia Mukt Bharat)",
            title = "Maternal & Adolescent Anemia Screening & IFA Drive",
            alertLevel = "ADVISORY",
            summary = "Weekly Village Health, Sanitation and Nutrition Days (VHSND) are providing digital hemoglobin checks and free Iron-Folic Acid supplementation.",
            preventionChecklist = listOf(
                "Pair iron-rich foods (moringa/drumstick leaves, ragi, jaggery, pulses) with Vitamin C (lemon/amla)",
                "Avoid drinking tea or coffee within 1 hour of meals as tannins block iron absorption",
                "Attend monthly VHSND at your local Anganwadi Centre"
            ),
            communityResourceContact = "Anganwadi / ANM Centre (104)",
            source = "Anemia Mukt Bharat, Ministry of Health & Family Welfare"
        ),
        PublicHealthAlert(
            id = "alert_tb",
            region = "National TB Elimination Program (Ni-kshay)",
            title = "Cough > 2 Weeks? Free TrueNat Test & ₹1,000/month Nutrition Support",
            alertLevel = "WATCH",
            summary = "Early detection of tuberculosis ensures 100% curable treatment. Under Ni-kshay Poshan Yojana, registered TB patients receive direct bank nutrition support.",
            preventionChecklist = listOf(
                "Get free sputum/TrueNat molecular test at nearest Govt CHC if cough, night sweats, or weight loss lasts > 2 weeks",
                "Never interrupt DOTS treatment early"
            ),
            communityResourceContact = "TB Helpline 1800-11-6666",
            source = "Central TB Division, MoHFW India"
        )
    )
}
