<?php
<#assign licenseFirst = "/* ">
<#assign licensePrefix = " * ">
<#assign licenseLast = " */">
<#include "../Licenses/license-${project.license}.txt">

/**
 * CakePHP Helper
 * @author ${user}
 */
class ${name}Helper extends AppHelper {
	public $helpers = array();
	public $settings = null;
	public $view = null;

	function __construct($settings){
		$this->settings = $settings;

		$this->view = ClassRegistry::getObject('view');
	}

	function beforeRender() {

	}

	function afterRender() {

	}

	function beforeLayout() {

	}

	function afterLayout() {

	}
}
