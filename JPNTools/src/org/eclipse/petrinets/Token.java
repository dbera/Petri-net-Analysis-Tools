package org.eclipse.petrinets;

public interface Token 
{
	public void setTokenValue(int value) throws SimulatorException;
	public void setTokenValue(String value) throws SimulatorException;
	public void setTokenValue(boolean value) throws SimulatorException;
	public void setTokenValue(double value) throws SimulatorException;
	
	public Type getTokenType();
}

class IntegerToken implements Token 
{
	Type type;
	int value;
	
	IntegerToken(int value)
	{
		this.type = Type.INT;
		this.value = value;
	}
	
	@Override
	public void setTokenValue(int value) {
		this.value = value;
	}

	@Override
	public void setTokenValue(String value) throws SimulatorException {
		throw new SimulatorException("Unsupported Token Data Type.");
	}

	@Override
	public void setTokenValue(boolean value) throws SimulatorException {
		throw new SimulatorException("Unsupported Token Data Type.");
	}

	@Override
	public void setTokenValue(double value) throws SimulatorException {
		throw new SimulatorException("Unsupported Token Data Type.");
	}

	@Override
	public Type getTokenType() {
		return type;
	}
}

class BooleanToken implements Token 
{
	Type type;
	boolean value;
	
	BooleanToken(boolean value)
	{
		this.type = Type.BOOL;
		this.value = value;
	}
	
	@Override
	public void setTokenValue(int value) throws SimulatorException {
		throw new SimulatorException("Unsupported Token Data Type.");
	}

	@Override
	public void setTokenValue(String value) throws SimulatorException {
		throw new SimulatorException("Unsupported Token Data Type.");
	}

	@Override
	public void setTokenValue(boolean value) {
		this.value = value;
	}

	@Override
	public void setTokenValue(double value) throws SimulatorException {
		throw new SimulatorException("Unsupported Token Data Type.");
	}

	@Override
	public Type getTokenType() {
		return type;
	}
}

class RealToken implements Token 
{
	Type type;
	double value;
	
	RealToken(double value)
	{
		this.type = Type.REAL;
		this.value = value;
	}
	
	@Override
	public void setTokenValue(int value) throws SimulatorException {
		throw new SimulatorException("Unsupported Token Data Type.");
	}

	@Override
	public void setTokenValue(String value) throws SimulatorException {
		throw new SimulatorException("Unsupported Token Data Type.");
	}

	@Override
	public void setTokenValue(boolean value) throws SimulatorException {
		throw new SimulatorException("Unsupported Token Data Type.");
	}

	@Override
	public void setTokenValue(double value) {
		this.value = value;
	}

	@Override
	public Type getTokenType() {
		return type;
	}
}

class StringToken implements Token 
{
	Type type;
	String value;
	
	StringToken(String value)
	{
		this.type = Type.STRING;
		this.value = value;
	}
	
	@Override
	public void setTokenValue(int value) throws SimulatorException {
		throw new SimulatorException("Unsupported Token Data Type.");
	}

	@Override
	public void setTokenValue(String value) {
		this.value = value;
	}

	@Override
	public void setTokenValue(boolean value) throws SimulatorException {
		throw new SimulatorException("Unsupported Token Data Type.");
	}

	@Override
	public void setTokenValue(double value) throws SimulatorException {
		throw new SimulatorException("Unsupported Token Data Type.");
	}
	
	@Override
	public Type getTokenType() {
		return type;
	}
}
