package org.openmrs.module.shr.atna.util;

import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.net.UnknownHostException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dcm4che3.audit.ActiveParticipant;
import org.dcm4che3.audit.AuditMessages;
import org.dcm4che3.audit.AuditMessages.ParticipantObjectIDTypeCode;
import org.dcm4che3.audit.ParticipantObjectDetail;
import org.dcm4che3.audit.ParticipantObjectIdentification;
import org.openmrs.Encounter;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.module.shr.atna.configuration.AtnaConfiguration;

/**
 * Audit meta-data utility
 * @author justi_000
 *
 */
public final class AuditMetaDataUtil {

	private final Log log = LogFactory.getLog(this.getClass());
	
	// Sync lock
	private static final Object s_syncLock = new Object();
	// Singleton
	private static AuditMetaDataUtil s_instance = null;
	
	// Get the configuration
	private final AtnaConfiguration m_configuration = AtnaConfiguration.getInstance();

	private static final String HL7_CXFORMAT = "%s^^^&%s&ISO";
	
	/**
	 * Audit meta-data utility
	 */
	private AuditMetaDataUtil()
	{
		
	}
	
	/**
	 * Gets or creates the singleton instance
	 * @return
	 */
	public static AuditMetaDataUtil getInstance()
	{
		if(s_instance == null)
			synchronized (s_syncLock) {
				if(s_instance == null)
					s_instance = new AuditMetaDataUtil();
			}
		return s_instance;
	}
	
	/**
	 * Get the OpenSHR Audit Participant
	 * @param userId Is the value to be placed in the userId
	 * @return
	 */
	public ActiveParticipant getOpenSHRActor(AuditMessages.RoleIDCode role, String endpointAddress)
	{
		ActiveParticipant retVal = new ActiveParticipant();
		retVal.setNetworkAccessPointTypeCode(AuditMessages.NetworkAccessPointTypeCode.MachineName);
		try {
			retVal.setNetworkAccessPointID(InetAddress.getLocalHost().getHostName());
		} catch (UnknownHostException e) {
			log.error(e);
		}
		retVal.setUserIsRequestor(role == AuditMessages.RoleIDCode.Source);
		retVal.setAlternativeUserID(ManagementFactory.getRuntimeMXBean().getName());
		retVal.setUserID(endpointAddress);
		retVal.getRoleIDCode().add(role);
		return retVal;
	}


	/**
	 * Get audit participant for a patient
	 */
	public ParticipantObjectIdentification getParticipant(Patient patient, String detail)
	{
	
		PatientIdentifier pid = null;
		for(PatientIdentifier id : patient.getIdentifiers())
			if(id.getIdentifierType().getName().equals(this.m_configuration.getEcidRoot()))
				pid = id;
			
		ParticipantObjectIdentification retVal = new ParticipantObjectIdentification();
		
		// Found ECID, use that
		if(pid != null)
			retVal.setParticipantObjectID(String.format(HL7_CXFORMAT, pid.getIdentifier(), pid.getIdentifierType().getName()));
		else
			retVal.setParticipantObjectID(String.format(HL7_CXFORMAT, patient.getId(), this.m_configuration.getPatientRoot()));
		
		// Type code
		retVal.setParticipantObjectTypeCode(AuditMessages.ParticipantObjectTypeCode.Person);
		retVal.setParticipantObjectTypeCodeRole(AuditMessages.ParticipantObjectTypeCodeRole.Patient);
		retVal.setParticipantObjectIDTypeCode(ParticipantObjectIDTypeCode.PatientNumber);
		
		if(detail != null)
		{
			ParticipantObjectDetail adetail = new ParticipantObjectDetail();
			adetail.setType(detail);
			retVal.getParticipantObjectDetail().add(adetail);
		}
		
		return retVal;
	}
	
	/**
	 * Participant object identification
	 */
	public ParticipantObjectIdentification getParticipant(Encounter enc, String lifecycle)
	{
		
		ParticipantObjectIdentification retVal = new ParticipantObjectIdentification();
		
		// Set properties
		retVal.setParticipantObjectID(String.format(HL7_CXFORMAT, enc.getId(), this.m_configuration.getEncounterRoot()));
		
		// Type code
		retVal.setParticipantObjectTypeCode(AuditMessages.ParticipantObjectTypeCode.SystemObject);
		retVal.setParticipantObjectTypeCodeRole(AuditMessages.ParticipantObjectTypeCodeRole.Resource);
		retVal.setParticipantObjectIDTypeCode(ParticipantObjectIDTypeCode.EncounterNumber);
		retVal.setParticipantObjectDataLifeCycle(lifecycle);

		return retVal;
	}
}
