package org.jenkinsci.plugins.rallyBuild;

public class UpdateArtifact {
	private final String objectId;
	private final String _ref;
	private final String _type;
	private final String description;
	private final String formattedId;
	private final String _refObjectName;
	
	public UpdateArtifact(String objectId,String _ref,String _type,String description, String formattedId, String _refObjectName){
		this.objectId=objectId;
		this._ref=_ref;
		this._type=_type;
		this.description=description;
		this.formattedId=formattedId;
		this._refObjectName=_refObjectName;
	}

	public String getObjectId() {
		return objectId;
	}

	public String get_ref() {
		return _ref;
	}

	public String get_type() {
		return _type;
	}

	public String getDescription() {
		return description;
	}

	public String getFormattedId() {
		return formattedId;
	}

	public String get_refObjectName() {
		return _refObjectName;
	}
}
