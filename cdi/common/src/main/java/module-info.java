module de.ruu.lib.cdi.common
{
	exports de.ruu.lib.cdi.common;
	opens   de.ruu.lib.cdi.common;

	requires jakarta.cdi;
	requires static lombok;
	requires org.slf4j;
}