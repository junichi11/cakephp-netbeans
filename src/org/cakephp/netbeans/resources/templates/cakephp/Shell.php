<?php
<#assign licenseFirst = "/* ">
<#assign licensePrefix = " * ">
<#assign licenseLast = " */">
<#include "../Licenses/license-${project.license}.txt">

/**
 * CakePHP Shell
 * @author ${user}
 */
class ${name}Shell extends Shell {

	public $uses = array();
	public $task = array();

	function main(){

	}
}
