package com.csio.hexagonal.infrastructure.rest.spec;

public class CorpibSpec {

    public static final String ENTITY = "CORPIB";

    public static final String DPR_SRC_ACT_INFO_SUMMARY =
            "Get account info from " + ENTITY;

    public static final String DPR_SRC_ACT_INFO_DESCRIPTION =
            "Calls CORPIB.dpr_src_act_info with user, organization, and account number inputs";

    public static final String DPR_SRC_ACT_INFO_EXAMPLE_NAME =
            "dpr_src_act_info Example";

    public static final String DPR_SRC_ACT_INFO_EXAMPLE_VALUE = """
            {
                "actNumber": "08533000197"
            }
            """;
}
