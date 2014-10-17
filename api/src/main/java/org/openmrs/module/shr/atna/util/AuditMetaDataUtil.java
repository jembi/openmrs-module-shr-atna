package org.openmrs.module.shr.atna.util;

import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.net.UnknownHostException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.marc.shic.atna.AuditActor;
import org.marc.shic.atna.AuditActorType;
import org.marc.shic.atna.AuditMetaData;
import org.marc.shic.atna.AuditParticipant;
import org.marc.shic.atna.ParticipantRoleType;
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
	public AuditActor getOpenSHRActor(AuditActorType role, String endpointAddress)
	{
		AuditActor retVal = new AuditActor();
		retVal.setActorType(role);
		retVal.setNetworkAccessPointTypeCode((short) 1);
		retVal.setUserIsRequestor(role == AuditActorType.Source);
		try {
			retVal.setNetworkEndpoint(InetAddress.getLocalHost().getHostName());
		} catch (UnknownHostException e) {
			log.error(e);
		}
		retVal.setAlternativeUserId(ManagementFactory.getRuntimeMXBean().getName());
		retVal.setIdentifier(endpointAddress);
		return retVal;
	}
	
	/**
	 * Get a remote actor node
	 */
	public AuditActor getRemoteActor(AuditActorType role, String networkEndpoint, String identifier)
	{
		AuditActor retVal = new AuditActor();
		retVal.setActorType(role);
		retVal.setNetworkAccessPointTypeCode((short) 1);
		retVal.setUserIsRequestor(role == AuditActorType.Source);
		retVal.setNetworkEndpoint(networkEndpoint);
		retVal.setIdentifier(identifier);
		return retVal;
	}

	/**
	 * Get audit participant for a patient
	 */
	public AuditParticipant getParticipant(Patient patient)
	{
	
		PatientIdentifier pid = null;
		for(PatientIdentifier id : patient.getIdentifiers())
			if(id.getIdentifierType().getName().equals(this.m_configuration.getEcidRoot()))
				pid = id;
			
		AuditParticipant retVal = new AuditParticipant();
		retVal.setParticipantRoleType(ParticipantRoleType.Patient);
		retVal.setTypeCode((short)1);
		retVal.setTypeCodeRole((short)1);
		
		// Found ECID, use that
		if(pid != null)
			retVal.setIdentifier(String.format(HL7_CXFORMAT, pid.getIdentifier(), pid.getIdentifierType().getName()));
		else
			retVal.setIdentifier(String.format(HL7_CXFORMAT, patient.getId(), this.m_configuration.getPatientRoot()));
		
		return retVal;
	}
	
	/**
	 * Creates a participant with the specified data
	 */
	public AuditParticipant createParticipant(int objectTypeCode, int objectRoleCode, ParticipantRoleType idTypeCode, String objectId)
	{
		AuditParticipant retVal = new AuditParticipant();
		retVal.setTypeCode((short)objectTypeCode);
		retVal.setTypeCodeRole((short)objectRoleCode);
		retVal.setParticipantRoleType(idTypeCode);
		retVal.setIdentifier(objectId);
		return retVal;
	}
}
