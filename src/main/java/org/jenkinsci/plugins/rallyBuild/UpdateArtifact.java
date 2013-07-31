package org.jenkinsci.plugins.rallyBuild;

public class UpdateArtifact {
	private final String objectId;
	private final String _ref;
	private final String _type;
	
	public UpdateArtifact(String objectId,String _ref,String _type){
		this.objectId=objectId;
		this._ref=_ref;
		this._type=_type;
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
}
