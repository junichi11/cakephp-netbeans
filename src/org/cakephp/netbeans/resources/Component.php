<?php
/**
 * CakePHP Component
 * @author ${user}
 */
class ${name}Component extends Object {
	//===============================================
	// property
	//===============================================
	public $components = array();
	public $settings = array();
	
	//===============================================
	// callback method
	//===============================================	
	function initialize(&$controller, $settings) {
		$this->controller = $controller;
		$this->settings = $settings;
	}
	
	function startup(&$controller){
		
	}
	
	function beroreRender() {

	}
	
	function beforeRedirect() {

	}
	
	function shutDown(&controller) {

	}
}
