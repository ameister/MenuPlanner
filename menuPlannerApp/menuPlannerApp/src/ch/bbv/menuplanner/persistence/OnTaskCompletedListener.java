package ch.bbv.menuplanner.persistence;



public interface OnTaskCompletedListener<ParamType>{

	void onTaskCompleted(ParamType result);
	
}
