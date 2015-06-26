package org.kuali.kfs.sys.service.impl;

import org.kuali.kfs.sys.dataaccess.DocumentStatsDao;
import org.kuali.kfs.sys.service.DocumentStatsService;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

@Transactional
public class DocumentStatsServiceImpl implements DocumentStatsService {
    protected DocumentStatsDao documentStatsDao;

    @Override
    public List<Map<String, Integer>> reportNumInitiatedDocsByDocType() {
        try {
            return getDocumentStatsDao().reportNumInitiatedDocsByDocType();
        } catch (SQLException sqle) {
            throw new RuntimeException(sqle);
        }
    }

    public DocumentStatsDao getDocumentStatsDao() {
        return documentStatsDao;
    }

    public void setDocumentStatsDao(DocumentStatsDao documentStatsDao) {
        this.documentStatsDao = documentStatsDao;
    }
}
