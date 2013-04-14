package config

import models.slick.SlickPersistenceComponent

/**
 * Author: Eric Jutrzenka
 * Date:   14/04/2013
 * Time:   15:15
 */

object Persistence extends SlickPersistenceComponent with CurrentPlayDb

