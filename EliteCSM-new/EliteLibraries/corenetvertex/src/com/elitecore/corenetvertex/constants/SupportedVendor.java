package com.elitecore.corenetvertex.constants;

public enum SupportedVendor {
        ERICSSION_5("Ericsson - 5"),
        CISCO_9("Cisco - 9"),
        THREE_COM_43("3Com - 43"),
        MERIT_61("Merit - 61"),
        NOKIA_94("Nokia - 94"),
        SHIVA_166("Shiva - 166"),
        ERRICSSION_VIG_193("Ericsson-ViG - 193"),
        CISCO_VPN5000_255("Cisco-VPN5000 - 255"),
        LIVINGSTON_307("Livingston - 307"),
        MICROSOFT_311("Microsoft - 311"),
        USR_429("USR - 429"),
        ASCEND_529("Ascend - 529"),
        ALCATEL_1430("Alcatel-1430 - 637"),
        ALCATEL_800("Alcatel - 800"),
        XEDIA_838("Xedia - 838"),
        ALLTEL_1049("Alltel - 1049"),
        FUNK_1411("Funk - 1411"),
        CYBERGUARD_1427("CyberGuard - 1457"),
        BAY_1584("Bay - 1584"),
        ORINOCO_1751("Orinoco - 1751"),
        FOUNDRY_1991("Foundry - 1991"),
        PACKETEER_2334("Packeteer - 2334"),
        REDBACK_2352("Redback - 2352"),
        JUNIPER_2636("Juniper - 2636"),
        NOTELE_CVX_2637("Nortel-CVX - 2637"),
        CISCO_VEN3000_3076("Cisco-VPN3000 - 3076"),
        COSINE_3085("Cosine - 3085"),
        NORTEL_SHASTA_3199("Nortel-Shasta - 3199"),
        NOMADIX_3390("Nomadix - 3309"),
        SPRINGRIDE_3551("SpringTide - 3551"),
        DSL_3561("DSL - 3561"),
        LUCENT_VID_3729("Lucent-VID - 3729"),
        LUCENT_4846("Lucent - 4846"),
        JUNIPER_UNISPHERE_4874("Juniper-Unisphere - 4874"),
        CISCO_BBSM_5263("Cisco-BBSM - 5263"),
        TGPP_5535("3GPP2 - 5535"),
        RIVERSTONE_5567("Riverstone - 5567"),
        ALCATEL_LICENT_IPD_6527("Alcatel-Lucent-IPD - 6527"),
        ALU_8610_7483("ALU-8610 - 7483"),
        AZAIRE_7751("Azaire - 7751"),
        SN1_8164("SN1 - 8164"),
        COLUBRIS_8744("Colubris - 8744"),
        ACME_9148("Acme - 9148"),
        TGPP_10415("3GPP - 10415"),
        VZW_12951("VzW - 12951"),
        WISPR_14122("WISPr - 14122"),
        CISCO_AIRESPACE_14179("Cisco-Airespace - 14179"),
        SYNIVERSE_14369("Syniverse - 14369"),
        TRAPEZE_14525("Trapeze - 14525"),
        ARUBA_14823("Aruba - 14823"),
        LUCENT_VID_ACESS_GROUP_19487("Lucent-VID-Access-Group - 19487"),
        WIMAX_24757("WiMAX - 24757");

    private String name;

    SupportedVendor(String name){
       this.name = name;
    }


    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
