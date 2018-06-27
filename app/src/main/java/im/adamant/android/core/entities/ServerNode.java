package im.adamant.android.core.entities;

import java.util.Objects;

public class ServerNode {

    public enum Status {
        CONNECTING,
        UNAVAILABLE,
        ACTIVE,
        CONNECTED
    }

    private String url;
    private int pingInMilliseconds = 0;
    private Status status = Status.CONNECTING;

    public ServerNode() {
    }

    public ServerNode(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getPingInMilliseconds() {
        return pingInMilliseconds;
    }

    public void setPingInMilliseconds(int pingInMilliseconds) {
        this.pingInMilliseconds = pingInMilliseconds;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ServerNode that = (ServerNode) o;
        return Objects.equals(url, that.url);
    }

    @Override
    public int hashCode() {

        return Objects.hash(url);
    }
}
