package org.teamspyder.spyderlib;

import java.util.function.BooleanSupplier;

public class MonitoredElement {
    protected BooleanSupplier m_monitor;
    protected BooleanSupplier m_reinit;
    protected int m_retries = 3;
    protected int m_errorCnt = 0;

    protected boolean m_hasError = true;

    public MonitoredElement(BooleanSupplier monitor, BooleanSupplier reinit) {
        m_monitor = monitor;
        m_reinit = reinit;
    }

    public boolean hasError() {
        return m_hasError;
    }

    public void maxRetries(int retries) {
        m_retries = retries;
    }
}