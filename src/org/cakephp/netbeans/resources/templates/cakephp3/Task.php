<?php
<#assign licenseFirst = "/* ">
<#assign licensePrefix = " * ">
<#assign licenseLast = " */">
<#include "../Licenses/license-${project.license}.txt">

use Cake\Console\Shell;

/**
 * CakePHP ${name}
 * @author ${user}
 */
class ${name} extends Shell {

	public $uses = array();

	public function execute(){

	}
}
