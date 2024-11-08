/* (C)2024 */
package org.jws.model;

public enum Timestep {
    TWENTY_FOUR_HOUR("24h");

    private final String value;

    Timestep(final String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
