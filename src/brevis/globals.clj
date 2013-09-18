#_"This file is part of brevis.                                                                                                                                                 
                                                                                                                                                                                     
    brevis is free software: you can redistribute it and/or modify                                                                                                           
    it under the terms of the GNU General Public License as published by                                                                                                             
    the Free Software Foundation, either version 3 of the License, or                                                                                                                
    (at your option) any later version.                                                                                                                                              
                                                                                                                                                                                     
    brevis is distributed in the hope that it will be useful,                                                                                                                
    but WITHOUT ANY WARRANTY; without even the implied warranty of                                                                                                                   
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the                                                                                                                    
    GNU General Public License for more details.                                                                                                                                     
                                                                                                                                                                                     
    You should have received a copy of the GNU General Public License                                                                                                                
    along with brevis.  If not, see <http://www.gnu.org/licenses/>.                                                                                                          
                                                                                                                                                                                     
Copyright 2012, 2013 Kyle Harrington"     

(ns brevis.globals)

(def enable-display-text true)

(def default-gui-state {:rotate-mode :none :translate-mode :none                                    
                        :rot-x 90 :rot-y -90 :rot-z -45
                        :shift-x 300 :shift-y 300 :shift-z -50;-30                                   
                        :last-report-time 0 :simulation-time 0})
  
(def #^:dynamic *gui-state* (atom default-gui-state))
(def #^:dynamic *gui-message-board* (atom (sorted-map))) 
(def #^:dynamic *app-thread* (atom nil))