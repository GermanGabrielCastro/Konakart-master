package com;

import org.apache.tiles.TilesApplicationContext;
import org.apache.tiles.factory.AbstractTilesContainerFactory;
import org.apache.tiles.startup.AbstractTilesInitializer;


// edit for verita-204
public class CustomTilesInitialitzer extends AbstractTilesInitializer {

	@Override
	protected AbstractTilesContainerFactory createContainerFactory(
			TilesApplicationContext context) {
		// TODO Auto-generated method stub
		//for change coupling test
		  return new CustomTilesContainerFactory();
	}

	// for change coupling by8 puneeth
}
