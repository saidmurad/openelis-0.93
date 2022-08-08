/*
 * The contents of this file are subject to the Mozilla Public License
 * Version 1.1 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations under
 * the License.
 *
 * The Original Code is OpenELIS code.
 *
 * Copyright (C) The Minnesota Department of Health.  All Rights Reserved.
 *
 * Contributor(s): CIRG, University of Washington, Seattle WA.
 */
package us.mn.state.health.lims.analyzerimport.analyzerreaders;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.validator.GenericValidator;
import org.hibernate.Transaction;

import us.mn.state.health.lims.analyzerimport.util.AnalyzerTestNameCache;
import us.mn.state.health.lims.analyzerimport.util.MappedTestName;
import us.mn.state.health.lims.analyzerimport.util.AnalyzerTestNameCache.AnalyzerType;
import us.mn.state.health.lims.analyzerresults.dao.AnalyzerResultsDAO;
import us.mn.state.health.lims.analyzerresults.valueholder.AnalyzerResults;
import us.mn.state.health.lims.common.exception.LIMSRuntimeException;
import us.mn.state.health.lims.common.util.DAOImplFactory;
import us.mn.state.health.lims.common.util.DateUtil;
import us.mn.state.health.lims.common.util.HibernateProxy;

import us.mn.state.health.lims.sample.dao.SampleDAO;
import us.mn.state.health.lims.sample.daoimpl.SampleDAOImpl;
import us.mn.state.health.lims.sample.valueholder.Sample;

@SuppressWarnings("unused")
public class SysmexReader extends AnalyzerLineInserter {

	private static final String CONTROL_ACCESSION_PREFIX = "QC-";

	private static int index = 0;
	private static final int ID_INSTRUMENT = index++;
	private static final int DATE = index++;
	private static final int TIME = index++;
	private static final int ACCESSION = index++;
	private static final int WBC = index++;
	private static final int RBC = index++;
	private static final int PLT = index++;
	private static final int HGB = index++;
	private static final int LYM = index++;
	private static final int MON = index++;
	private static final int NEU = index++;
	private static final int EO = index++;
	private static final int BAS = index++;
	private static final int LYM_PER = index++;
	private static final int MON_PER = index++;
	private static final int NEU_PER = index++;
	private static final int EO_PER = index++;
	private static final int BAS_PER = index++;
	private static final int HCT = index++;
	private static final int MCV = index++;
	private static final int MCH = index++;
	private static final int MCHC = index++;
	private static final int RDWsd = index++;
	private static final int RDWcv = index++;
	private static final int PDWsd = index++;
	private static final int PDWcv = index++;
	private static final int MPV = index++;
	private static final int PCT = index++;
	private static final int PLCR = index++;
	private static final int PLCC = index++;

	/*private static final int ID_INSTRUMENT = index++;
	private static final int DATE = index++;
	private static final int TIME = index++;
	private static final int RACK = index++;
	private static final int TUBE = index++;
	private static final int ACCESSION = index++;
	private static final int ACCESSION_INFO = index++;
	private static final int METHOD = index++;
	private static final int ID_PATIENT = index++;
	private static final int ANALYSIS_INFO = index++;
	private static final int POS_NEG = index++;
	private static final int POS_DIFF = index++;
	private static final int POS_MORF = index++;
	private static final int POS_COMP = index++;
	private static final int ERR_FUNC = index++;
	private static final int ERR_RESULT = index++;
	private static final int INFO_REQUEST = index++;
	private static final int GB_ABNORMAL = index++;
	private static final int GB_SUSPICIOUS = index++;
	private static final int GR_ABNORMAL = index++;
	private static final int GR_SUSPICIOUS = index++;
	private static final int PLQ_ABNORMAL = index++;
	private static final int PLQ_SUSPICIOUS = index++;
	private static final int INFO_UNITS = index++;
	private static final int INFO_PLQ = index++;
	private static final int VALID = index++;
	private static final int RET_MESSAGES = index++;
	private static final int DELTA_MESSAGES = index++;
	private static final int SAMPLE_COMMENT = index++;
	private static final int PATIENT_NAME = index++;
	private static final int PATIENT_BIRTH_DATE = index++;
	private static final int PATIENT_GENDER = index++;
	private static final int PATIENT_COMMENT = index++;
	private static final int SERVICE = index++;
	private static final int MEDS = index++;
	private static final int TRANSMISSION_PARMS = index++;
	private static final int SEQUENECE_COUNT = index++;
	private static final int GB_10_uL = index++;
    private static final int GB_10_uL = index++;
	private static final int GB_M = index++;
	private static final int GR_100000_uL = index++;
	private static final int GR_M = index++;
	private static final int HBG_g_L = index++;
	private static final int HBG_M = index++;
	private static final int HCT_10_NEG_1_PER = index++;
	private static final int HCT_M = index++;
	private static final int VGM_10_NEG_1_fL = index++;
	private static final int VGM_M = index++;
	private static final int TCMH_10_NEG_1_pg = index++;
	private static final int TCMH_M = index++;
	private static final int CCMH_g_L = index++;
	private static final int CCMH_M = index++;
	private static final int PLQ_10_3_uL = index++;
	private static final int PLQ_M = index++;
	private static final int IDR_SD_10_NEG_1_fL = index++;
	private static final int IDR_SD_M = index++;
	private static final int IDR_CV_10_NEG_1_PER = index++;
	private static final int IDR_CV_M = index++;
	private static final int IDP = index++;
	private static final int IDP_M = index++;
	private static final int VPM_10_NEG_1_fL = index++;
	private static final int VPM_M = index++;
	private static final int P_RGC_10_NEG_1_PER = index++;
	private static final int P_RGC_M = index++;
	private static final int PCT_10_NEG_2_PER = index++;
	private static final int PCT_M = index++;
	private static final int NEUT_COUNT_10_uL = index++;
	private static final int NEUT_COUNT_M = index++;
	private static final int LYMPH_COUNT_10_uL = index++;
	private static final int LYMPH_COUNT_M = index++;
	private static final int MONO_COUNT_10_uL = index++;
	private static final int MONO_COUNT_M = index++;
	private static final int EO_COUNT_10_uL = index++;
	private static final int EO_COUNT_M = index++;
	private static final int BASO_COUNT_10_uL = index++;
	private static final int BASO_COUNT_M = index++;
	private static final int NEUT_PER_10_NEG_1_PER = index++;
	private static final int NEUT_PER_M = index++;
	private static final int LYMPH_PER_10_NEG_1_PER = index++;
	private static final int LYMPH_PER_M = index++;
	private static final int MONO_PER_10_NEG_1_PER = index++;
	private static final int MONO_PER_M = index++;
	private static final int EO_PER_10_NEG_1_PER = index++;
	private static final int EO_PER_M = index++;
	private static final int BASO_PER_10_NEG_1_PER = index++;
	private static final int BASO_PER_M = index++;
	private static final int RET_COUNT_10_2_uL = index++;
	private static final int RET_COUNT_M = index++;
	private static final int RET_PER_10_NEG_2_PER = index++;
	private static final int RET_PER_M = index++;
	private static final int LFR_10_NEG_1_PER = index++;
	private static final int LFR_M = index++;
	private static final int MFR_10_NEG_1_PER = index++;
	private static final int MFR_M = index++;
	private static final int HFR_10_NEG_1_PER = index++;
	private static final int HFR_M = index++;
	private static final int IRF_10_NEG_1_PER = index++;
	private static final int IRF_M = index++;
	private static final int IG_COUNT_10_uL = index++;
	private static final int IG_COUNT_M = index++;
	private static final int IG_PER_10_NEG_1_PER = index++;
	private static final int IG_PER_M = index++;
	private static final int NEUT_COUNT_AND_10_uL = index++;
	private static final int NEUT_COUNT_AND_M = index++;
	private static final int NEUT_PER_AND_10_NEG_1_PER = index++;
	private static final int NEUT_PER_AND_M = index++;
	private static final int NRBC_PLUS_W_10_uL = index++;
	private static final int NRBC_PLUS_W_M = index++;
	private static final int LYMPH_COUNT_AND_10_uL = index++;
	private static final int LYMPH_COUNT_AND_M = index++;
	private static final int LYMPH_PER_AND_10_NEG_1_PER = index++;
	private static final int LYMPH_PER_AND_M = index++;
	private static final int OTHER_COUNT_10_uL = index++;
	private static final int OTHER_COUNT_M = index++;
	private static final int OTHER_PER_10_NEG_1_PER = index++;
	private static final int OTHER_PER_M = index++;
	private static final int GR_O_10_4_uL = index++;
	private static final int GR_O_M = index++;
	private static final int PLQ_O_10_3_uL = index++;
	private static final int PLQ_O_M = index++;
	private static final int IP_Abn_GB_Scatterg_GB_Anorm = index++;
	private static final int IP_Abn_GB_NEUTROPENIA = index++;
	private static final int IP_Abn_GB_NEUTROPHILIE = index++;
	private static final int IP_Abn_GB_LYMPHOPENIA = index++;
	private static final int IP_Abn_GB_LYMPHCYTOSIS = index++;
	private static final int IP_Abn_GB_MONOCYTOSIS = index++;
	private static final int IP_Abn_GB_EOSINOPHILIE = index++;
	private static final int IP_Abn_GB_BASOPHILIE = index++;
	private static final int IP_Abn_GB_LEUCOPENIA = index++;
	private static final int IP_Abn_GB_LEUCOCYTOSIS = index++;
	private static final int IP_Abn_GR_Dist_GR_An = index++;
	private static final int IP_Abn_GR_D_pop_GR = index++;
	private static final int IP_Abn_GR_ANISOCYTOSIS = index++;
	private static final int IP_Abn_GR_MICROCYTOSIS = index++;
	private static final int IP_Abn_GR_MACROCYTOSIS = index++;
	private static final int IP_Abn_GR_HYPOCHROMIA = index++;
	private static final int IP_Abn_GR_ANEMIA = index++;
	private static final int IP_Abn_GR_ERYTHROCYTOSIS = index++;
	private static final int IP_Abn_GR_Scatterg_RET_Anorm = index++;
	private static final int IP_Abn_GR_RETICULOCYTOSIS = index++;
	private static final int IP_Abn_PLQ_Dist_PLQ_An = index++;
	private static final int IP_Abn_PLQ_THROMBOCYTOPENIA = index++;
	private static final int IP_Abn_PLQ_THROMBOCYTOSIS = index++;
	private static final int IP_Abn_PLQ_Scatterg_PLQ_Anorm = index++;
	private static final int IP_SUS_GB_Blasts = index++;
	private static final int IP_SUS_GB_Gra_Immat = index++;
	private static final int IP_SUS_GB_Dev_Gauche = index++;
	private static final int IP_SUS_GB_Lympho_Aty = index++;
	private static final int IP_SUS_GB_Anor_Ly_Blasts = index++;
	private static final int IP_SUS_GB_NRBC = index++;
	private static final int IP_SUS_GB_Res_GR_Lyse = index++;
	private static final int IP_SUS_GR_Agglut_GR = index++;
	private static final int IP_SUS_GR_Turb_HGB_Interf = index++;
	private static final int IP_SUS_GR_IRON_DEFICIENCY = index++;
	private static final int IP_SUS_GR_Def_HGB = index++;
	private static final int IP_SUS_GR_Fragments = index++;
	private static final int IP_SUS_PLQ_Agg_PLQ = index++;
	private static final int IP_SUS_PLQ_Agg_PLQ_S = index++;
	private static final int DEFAULT_INFO = index++;
	private static final int Qflag_Blasts = index++;
	private static final int Qflag_Gra_Immat = index++;
	private static final int Qflag_Dev_Gauche = index++;
	private static final int Qflag_Lympho_Aty = index++;
	private static final int Qflag_NRBC = index++;
	private static final int Qflag_Abn_Ly_Bla = index++;
	private static final int Qflag_Res_GR_Lysis = index++;
	private static final int Qflag_Agglut_GR = index++;
	private static final int Qflag_Turb_HGB = index++;
	private static final int Qflag_IRON_DEFICIENCY = index++;
	private static final int Qflag_Def_HGB = index++;
	private static final int Qflag_Fragments = index++;
	private static final int Qflag_Agg_PLQ = index++;
	private static final int Qflag_Agg_PLQ_S = index++; */
	private static final int columns = index++;

	private static final String DELIMITER = ",";

	private static final String DATE_PATTERN = "dd/MM/yyyy HH:mm:ss";
	private static String[] testNameIndex = new String[columns];
	private static String[] unitsIndex = new String[columns];
	private static Boolean[] readOnlyIndex = new Boolean[columns];
	private static int[] scaleIndex = new int[columns];
	private static int[] orderedTestIndexs = new int[19];

	{
		/*testNameIndex[GB_10_uL] = "GB_10_uL";
		testNameIndex[GR_100000_uL] = "GR_100000_uL";
		testNameIndex[NEUT_PER_10_NEG_1_PER] = "NEUT_PER_10_NEG_1_PER";
		testNameIndex[HBG_g_L] = "HBG_g_L";
		testNameIndex[LYMPH_PER_10_NEG_1_PER] = "LYMPH_PER_10_NEG_1_PER";
		testNameIndex[HCT_10_NEG_1_PER] = "HCT_10_NEG_1_PER";
		testNameIndex[MONO_PER_10_NEG_1_PER] = "MONO_PER_10_NEG_1_PER";
		testNameIndex[VGM_10_NEG_1_fL] = "VGM_10_NEG_1_fL";
		testNameIndex[EO_PER_10_NEG_1_PER] = "EO_PER_10_NEG_1_PER";
		testNameIndex[TCMH_10_NEG_1_pg] = "TCMH_10_NEG_1_pg";
		testNameIndex[BASO_PER_10_NEG_1_PER] = "BASO_PER_10_NEG_1_PER";
		testNameIndex[CCMH_g_L] = "CCMH_g_L";
		testNameIndex[PLQ_10_3_uL] = "PLQ_10_3_uL";

		testNameIndex[NEUT_COUNT_10_uL] = "NE#";
		testNameIndex[MONO_COUNT_10_uL] = "MO#";
		testNameIndex[BASO_COUNT_10_uL] = "BA#";
		testNameIndex[LYMPH_COUNT_10_uL] = "LY#";
		testNameIndex[EO_COUNT_10_uL] = "EO#";
		testNameIndex[IDR_SD_10_NEG_1_fL] = "Platelet_Count";
		testNameIndex[IDR_CV_10_NEG_1_PER] = "ESR"; */

		testNameIndex[WBC] = "WBC";
		testNameIndex[RBC] = "RBC";
		testNameIndex[PLT] = "PLT";
		testNameIndex[HGB] = "HGB";
		testNameIndex[LYM] = "LYM";
		testNameIndex[MON] = "MON";
		testNameIndex[NEU] = "NEU";
		testNameIndex[EO] = "EO";
		testNameIndex[BAS] = "BAS";
		testNameIndex[LYM_PER] = "LYM%";
		testNameIndex[MON_PER] = "MON%";
		testNameIndex[NEU_PER] = "NEU%";
		testNameIndex[EO_PER] = "EO%";

		testNameIndex[BAS_PER] = "BAS%";
		testNameIndex[HCT] = "HCT";
		testNameIndex[MCV] = "MCV";
		testNameIndex[MCH] = "MCH";
		testNameIndex[MCHC] = "MCHC";
		//testNameIndex[RDWsd] = "RDWsd";
		testNameIndex[RDWcv] = "RDWcv";

		testNameIndex[MPV] = "MPV";

        /*testNameIndex[PDWsd] = "PDWsd";
        testNameIndex[PDWcv] = "PDWcv";
        testNameIndex[MPV] = "MPV";
        testNameIndex[PCT] = "PCT";
        testNameIndex[PLCR] = "PLCR";
        testNameIndex[PLCC] = "PLCC"; */



		unitsIndex[WBC] = "10^3/ul";
		unitsIndex[RBC] = "10^6/ul";
		unitsIndex[PLT] = "10^3/ul";
		unitsIndex[HGB] = "g/dl";
		unitsIndex[LYM] = "10^3/ul";
		unitsIndex[MON] = "10^3/ul";
		unitsIndex[NEU] = "10^3/ul";
		unitsIndex[EO] = "10^3/ul";
		unitsIndex[BAS] = "10^3/ul";
		unitsIndex[LYM_PER] = "%";
		unitsIndex[MON_PER] = "%";
		unitsIndex[NEU_PER] = "%";
		unitsIndex[EO_PER] = "%";

		unitsIndex[BAS_PER] = "%";
		unitsIndex[HCT] = "%";
		unitsIndex[MCV] = "fl";
		unitsIndex[MCH] = "pg";
		unitsIndex[MCHC] = "g/dl";
		//unitsIndex[RDWsd] = " ";
		unitsIndex[RDWcv] = "%";

		unitsIndex[MPV] = "%";

/*		unitsIndex[PDWsd] = " ";
        unitsIndex[PDWcv] = " ";
        unitsIndex[MPV] = " ";
        unitsIndex[PCT] = " ";
        unitsIndex[PLCR] = " ";
        unitsIndex[PLCC] = " "; */

		for( int i = 0; i < readOnlyIndex.length; i++){
			readOnlyIndex[i] = Boolean.FALSE;
		}
/*
		readOnlyIndex[NEUT_COUNT_10_uL] = Boolean.TRUE;
		readOnlyIndex[MONO_COUNT_10_uL] = Boolean.TRUE;
		readOnlyIndex[BASO_COUNT_10_uL] = Boolean.TRUE;
		readOnlyIndex[LYMPH_COUNT_10_uL] = Boolean.TRUE;
		readOnlyIndex[EO_COUNT_10_uL] = Boolean.TRUE;
*/
		scaleIndex[WBC] = 1;
		scaleIndex[RBC] = 1;
		scaleIndex[PLT] = 1;
		scaleIndex[HGB] = 1;
		scaleIndex[LYM] = 1;
		scaleIndex[MON] = 1;
		scaleIndex[NEU] = 1;
		scaleIndex[EO] = 1;
		scaleIndex[BAS] = 1;
		scaleIndex[LYM_PER] = 1;
		scaleIndex[MON_PER] = 1;
		scaleIndex[NEU_PER] = 1;
		scaleIndex[EO_PER] = 1;
		scaleIndex[BAS_PER] = 1;
		scaleIndex[HCT] = 1;
		scaleIndex[MCV] = 1;
		scaleIndex[MCH] = 1;
		scaleIndex[MCHC] = 1;
		scaleIndex[RDWsd] = 1;
		scaleIndex[RDWcv] = 1;

		scaleIndex[MPV] = 1;

		/*scaleIndex[PDWsd] = 1;
        scaleIndex[PDWcv] = 1;
        scaleIndex[MPV] = 1;
        scaleIndex[PCT] = 1;
        scaleIndex[PLCR] = 1;
        scaleIndex[PLCC] = 1; */



		orderedTestIndexs[0] =WBC;
		orderedTestIndexs[1] = RBC;
		orderedTestIndexs[2] = PLT;
		orderedTestIndexs[3] = HGB;
		orderedTestIndexs[4] = LYM;
		orderedTestIndexs[5] = MON;
		orderedTestIndexs[6] = NEU;
		orderedTestIndexs[7] = EO;
		orderedTestIndexs[8] = BAS;
		orderedTestIndexs[9] = LYM_PER;
		orderedTestIndexs[10] = MON_PER;
		orderedTestIndexs[11] = NEU_PER;
		orderedTestIndexs[12] = EO_PER;

		orderedTestIndexs[13] = BAS_PER;
		orderedTestIndexs[14] = HCT;
		orderedTestIndexs[15] = MCV;
		orderedTestIndexs[16] = MCH;
		orderedTestIndexs[17] = MCHC;
		orderedTestIndexs[18] = RDWcv;

		/*orderedTestIndexs[20] = PDWsd;
        orderedTestIndexs[21] = PDWcv;
        orderedTestIndexs[22] = MPV;
        orderedTestIndexs[23] = PCT;
        orderedTestIndexs[24] = PLCR;
        orderedTestIndexs[25] = PLCC;*/


		/*
		GB_10_uL              WBC   100
		GR_100000_uL          RBC   100
		NEUT_PER_10_NEG_1_PER NE%    10
		HBG_g_L               HGB    10
		LYMPH_PER_10_NEG_1_PER LY%   10
		HCT_10_NEG_1_PER      HCT    10
		MONO_PER_10_NEG_1_PER MO%    10
		VGM_10_NEG_1_fL       MVC    10
		EO_PER_10_NEG_1_PER   EO%    10
		TCMH_10_NEG_1_pg      MCH    10
		BASO_PER_10_NEG_1_PER BA%    10
		CCMH_g_L              MCHC   10
		PLQ_10_3_uL           PLT     1

		NEUT_COUNT_10_uL      NE#   100
		MONO_COUNT_10_uL      MO#   100
		BASO_COUNT_10_uL      BA#    10
		LYMPH_COUNT_10_uL     LY#   100
		EO_COUNT_10_uL        EO#   100
		IDR_SD_10_NEG_1_FL    Platelet Count
		IDR_CV_10_NEG_1_PER	  ESR
		 */
	}

	private SampleDAO sampleDAO = new SampleDAOImpl();

	public boolean insert(List<String> lines, String currentUserId) {
		boolean successful = true;

		List<AnalyzerResults> results = new ArrayList<AnalyzerResults>();

		for (int i = 1; i < lines.size(); i++) {
			addAnalyzerResultFromLine(results, lines);
		}

		if (results.size() > 0) {

			Transaction tx = HibernateProxy.beginTransaction();

			try {

				persistResults(results, currentUserId);

				tx.commit();

			} catch (LIMSRuntimeException lre) {
				tx.rollback();
				successful = false;
			} finally {
				HibernateProxy.closeSession();
			}
		}

		return successful;
	}

	private void addAnalyzerResultFromLine(List<AnalyzerResults> results, List<String> lines) {
		String[] header = lines.get(0).split(DELIMITER);
		String[] fields = lines.get(1).split(DELIMITER);
		String analyzerAccessionNumber = fields[ACCESSION];

		// Check if sample already exists
		Sample sample = sampleDAO.getSampleByAccessionNumber(analyzerAccessionNumber);
		if( sample == null)
			return;

		AnalyzerReaderUtil readerUtil = new AnalyzerReaderUtil();

		Timestamp timestamp = DateUtil.convertStringDateToTimestampWithPattern(fields[DATE] + " " + fields[TIME], DATE_PATTERN);

		List<AnalyzerResults> readOnlyResults = new ArrayList<AnalyzerResults>();

		//the reason for the indirection is to get the order of tests correct
		for (int i = 4; i < header.length; i++) {
			String testName = header[i];
			String testResult = fields[i];
			if (!GenericValidator.isBlankOrNull(header[i])) {
				MappedTestName mappedName = AnalyzerTestNameCache.instance().getMappedTest(AnalyzerType.SYSMEX_XT_2000, testName);

				if( mappedName == null){
					mappedName = AnalyzerTestNameCache.instance().getEmptyMappedTestName(AnalyzerType.SYSMEX_XT_2000, testName);
				}

				AnalyzerResults analyzerResults = new AnalyzerResults();

				analyzerResults.setAnalyzerId(mappedName.getAnalyzerId());

				double result = Double.NaN;

				try{
					result = Double.parseDouble(testResult)/ scaleIndex[ArrayUtils.indexOf(testNameIndex, testName)];
				}catch( NumberFormatException nfe){
					//no-op -- defaults to NAN
				}

				analyzerResults.setResult(String.valueOf(result));
				analyzerResults.setUnits(unitsIndex[ArrayUtils.indexOf(testNameIndex, testName)]);
				analyzerResults.setCompleteDate(timestamp);
				analyzerResults.setTestId(mappedName.getTestId());
				analyzerResults.setAccessionNumber(analyzerAccessionNumber);
				analyzerResults.setTestName(mappedName.getOpenElisTestName());
				analyzerResults.setReadOnly(readOnlyIndex[ArrayUtils.indexOf(testNameIndex, testName)]);

				if (analyzerAccessionNumber != null) {
					analyzerResults.setIsControl(analyzerAccessionNumber.startsWith(CONTROL_ACCESSION_PREFIX));
				} else {
					analyzerResults.setIsControl(false);
				}

				if( analyzerResults.isReadOnly()){
					readOnlyResults.add(analyzerResults);
				}else{
					results.add(analyzerResults);
				}

				AnalyzerResults resultFromDB = readerUtil.createAnalyzerResultFromDB(analyzerResults);
				if( resultFromDB != null){
					results.add(resultFromDB);
				}
			}
		}

		results.addAll(readOnlyResults);
	}

	@Override
	public String getError() {
		return "Sysmex analyzer unable to write to database";
	}

}
