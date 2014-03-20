<?php
<#assign licenseFirst = "/* ">
<#assign licensePrefix = " * ">
<#assign licenseLast = " */">
<#include "../Licenses/license-${project.license}.txt">

/**
 * CakePHP Component
 * @author ${user}
 */
class ${name}Component extends Object {
	public $components = array();
	public $settings = array();

	function initialize(&$controller, $settings) {
		$this->controller = $controller;
		$this->settings = $settings;
	}

	function startup(&$controller){

	}

	function beforeRender() {

	}

	function beforeRedirect() {

	}

	function shutDown(&$controller) {

	}
}
