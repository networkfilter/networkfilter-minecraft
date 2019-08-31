package io.nilsdev.bungee.networkfilter.json;

public class ApiResponse {

    private String ip;
    private String asn;
    private String org;
    private boolean blocked;
    private String blockedType;
    private boolean cached;
    private int took;

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getAsn() {
        return asn;
    }

    public void setAsn(String asn) {
        this.asn = asn;
    }

    public String getOrg() {
        return org;
    }

    public void setOrg(String org) {
        this.org = org;
    }

    public boolean isBlocked() {
        return blocked;
    }

    public void setBlocked(boolean blocked) {
        this.blocked = blocked;
    }

    public String getBlockedType() {
        return blockedType;
    }

    public void setBlockedType(String blockedType) {
        this.blockedType = blockedType;
    }

    public boolean isCached() {
        return cached;
    }

    public void setCached(boolean cached) {
        this.cached = cached;
    }

    public int getTook() {
        return took;
    }

    public void setTook(int took) {
        this.took = took;
    }

    @Override
    public String toString() {
        return "ApiResponse{" +
                "ip='" + ip + '\'' +
                ", asn='" + asn + '\'' +
                ", org='" + org + '\'' +
                ", blocked=" + blocked +
                ", blockedType='" + blockedType + '\'' +
                ", cached=" + cached +
                ", took=" + took +
                '}';
    }
}
