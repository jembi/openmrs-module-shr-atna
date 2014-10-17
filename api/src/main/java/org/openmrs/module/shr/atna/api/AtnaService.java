package org.openmrs.module.shr.atna.api;

import org.marc.shic.atna.AuditMetaData;
import org.openmrs.api.OpenmrsService;

/**
 * Service which allows for sending of ATNA messages
 * @author justi_000
 *
 */
public interface AtnaService extends OpenmrsService {

	/**
	 * Send an audit via this service
	 */
	void sendAudit(AuditMetaData auditData);
	
	
}
