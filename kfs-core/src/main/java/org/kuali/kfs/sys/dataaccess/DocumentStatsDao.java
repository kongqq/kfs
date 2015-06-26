package org.kuali.kfs.sys.dataaccess;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public interface DocumentStatsDao {
    public List<Map<String, Integer>> reportNumInitiatedDocsByDocType(int limit, int days) throws SQLException;

    Map<String, Map<String, Integer>> reportCompletedActionRequestsByPrincipal(int limit);

    Map<String, Map<String, Integer>> reportUncompletedActionRequestsByPrincipal(int limit);

    Map<String, Integer> reportUncompletedActionRequestsByType();

    Map<String, Integer> reportCompletedActionRequestsByType();
}