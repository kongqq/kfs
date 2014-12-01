--
-- The Kuali Financial System, a comprehensive financial management system for higher education.
-- 
-- Copyright 2005-2014 The Kuali Foundation
-- 
-- This program is free software: you can redistribute it and/or modify
-- it under the terms of the GNU Affero General Public License as
-- published by the Free Software Foundation, either version 3 of the
-- License, or (at your option) any later version.
-- 
-- This program is distributed in the hope that it will be useful,
-- but WITHOUT ANY WARRANTY; without even the implied warranty of
-- MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
-- GNU Affero General Public License for more details.
-- 
-- You should have received a copy of the GNU Affero General Public License
-- along with this program.  If not, see <http://www.gnu.org/licenses/>.
--

-- This script will migrate a column in a table from the former Rice country 
-- codes which were based on FIPS 10-4 (with some minor differences) to the new
-- Rice country codes which are based on ISO 3166-1.  This script may not 
-- properly execute on columns with a primary key or unique constraint as some
-- of the former codes have merged (i.e. - all US Minor Outlying Islands have 
-- been unified under a single code, UM).  This script also does not take any 
-- action on codes that are not part of the list of former Rice country codes.
--
-- Table Name: 	PUR_PO_CPTL_AST_LOC_T
-- Column Name: CPTL_AST_CNTRY_CD
--
-- In order to avoid collisions between the former codes and the new codes, the 
-- codes are first changed to an interim, unique code.  Once that change is 
-- complete, they are changed to the new, correct code.
--
-- Change to temporary code
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='A0' where CPTL_AST_CNTRY_CD='AA';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='A1' where CPTL_AST_CNTRY_CD='AC';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='A3' where CPTL_AST_CNTRY_CD='AG';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='A4' where CPTL_AST_CNTRY_CD='AJ';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='A7' where CPTL_AST_CNTRY_CD='AN';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='A9' where CPTL_AST_CNTRY_CD='AQ';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='B1' where CPTL_AST_CNTRY_CD='AS';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='B2' where CPTL_AST_CNTRY_CD='AT';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='B3' where CPTL_AST_CNTRY_CD='AU';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='B4' where CPTL_AST_CNTRY_CD='AV';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='B5' where CPTL_AST_CNTRY_CD='AY';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='B6' where CPTL_AST_CNTRY_CD='BA';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='B8' where CPTL_AST_CNTRY_CD='BC';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='B9' where CPTL_AST_CNTRY_CD='BD';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='C1' where CPTL_AST_CNTRY_CD='BF';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='C2' where CPTL_AST_CNTRY_CD='BG';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='C3' where CPTL_AST_CNTRY_CD='BH';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='C4' where CPTL_AST_CNTRY_CD='BK';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='C5' where CPTL_AST_CNTRY_CD='BL';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='C6' where CPTL_AST_CNTRY_CD='BM';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='C7' where CPTL_AST_CNTRY_CD='BN';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='C8' where CPTL_AST_CNTRY_CD='BO';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='C9' where CPTL_AST_CNTRY_CD='BP';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='D0' where CPTL_AST_CNTRY_CD='BQ';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='D2' where CPTL_AST_CNTRY_CD='BS';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='D4' where CPTL_AST_CNTRY_CD='BU';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='D6' where CPTL_AST_CNTRY_CD='BX';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='D7' where CPTL_AST_CNTRY_CD='BY';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='D9' where CPTL_AST_CNTRY_CD='CB';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='E0' where CPTL_AST_CNTRY_CD='CD';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='E1' where CPTL_AST_CNTRY_CD='CE';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='E2' where CPTL_AST_CNTRY_CD='CF';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='E3' where CPTL_AST_CNTRY_CD='CG';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='E4' where CPTL_AST_CNTRY_CD='CH';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='E5' where CPTL_AST_CNTRY_CD='CI';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='E6' where CPTL_AST_CNTRY_CD='CJ';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='E7' where CPTL_AST_CNTRY_CD='CK';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='E9' where CPTL_AST_CNTRY_CD='CN';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='F1' where CPTL_AST_CNTRY_CD='CQ';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='F2' where CPTL_AST_CNTRY_CD='CR';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='F3' where CPTL_AST_CNTRY_CD='CS';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='F4' where CPTL_AST_CNTRY_CD='CT';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='F7' where CPTL_AST_CNTRY_CD='CW';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='F9' where CPTL_AST_CNTRY_CD='CZ';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='G0' where CPTL_AST_CNTRY_CD='DA';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='G2' where CPTL_AST_CNTRY_CD='DO';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='G3' where CPTL_AST_CNTRY_CD='DR';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='G6' where CPTL_AST_CNTRY_CD='EI';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='G7' where CPTL_AST_CNTRY_CD='EK';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='G8' where CPTL_AST_CNTRY_CD='EN';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='H0' where CPTL_AST_CNTRY_CD='ES';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='H2' where CPTL_AST_CNTRY_CD='EU';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='H3' where CPTL_AST_CNTRY_CD='EZ';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='H4' where CPTL_AST_CNTRY_CD='FA';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='H5' where CPTL_AST_CNTRY_CD='FG';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='I0' where CPTL_AST_CNTRY_CD='FP';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='I1' where CPTL_AST_CNTRY_CD='FQ';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='I3' where CPTL_AST_CNTRY_CD='FS';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='I4' where CPTL_AST_CNTRY_CD='GA';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='I5' where CPTL_AST_CNTRY_CD='GB';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='I6' where CPTL_AST_CNTRY_CD='GE';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='I7' where CPTL_AST_CNTRY_CD='GG';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='J0' where CPTL_AST_CNTRY_CD='GJ';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='J1' where CPTL_AST_CNTRY_CD='GK';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='J3' where CPTL_AST_CNTRY_CD='GM';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='J4' where CPTL_AST_CNTRY_CD='GO';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='J6' where CPTL_AST_CNTRY_CD='GQ';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='J9' where CPTL_AST_CNTRY_CD='GV';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='K1' where CPTL_AST_CNTRY_CD='GZ';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='K2' where CPTL_AST_CNTRY_CD='HA';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='K5' where CPTL_AST_CNTRY_CD='HO';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='K6' where CPTL_AST_CNTRY_CD='HQ';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='K9' where CPTL_AST_CNTRY_CD='IC';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='L4' where CPTL_AST_CNTRY_CD='IP';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='L6' where CPTL_AST_CNTRY_CD='IS';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='L8' where CPTL_AST_CNTRY_CD='IV';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='L9' where CPTL_AST_CNTRY_CD='IY';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='M0' where CPTL_AST_CNTRY_CD='IZ';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='M1' where CPTL_AST_CNTRY_CD='JA';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='M4' where CPTL_AST_CNTRY_CD='JN';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='M6' where CPTL_AST_CNTRY_CD='JQ';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='M7' where CPTL_AST_CNTRY_CD='JU';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='N0' where CPTL_AST_CNTRY_CD='KN';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='N1' where CPTL_AST_CNTRY_CD='KQ';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='N2' where CPTL_AST_CNTRY_CD='KR';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='N3' where CPTL_AST_CNTRY_CD='KS';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='N4' where CPTL_AST_CNTRY_CD='KT';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='N5' where CPTL_AST_CNTRY_CD='KU';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='N8' where CPTL_AST_CNTRY_CD='LE';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='N9' where CPTL_AST_CNTRY_CD='LG';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='O0' where CPTL_AST_CNTRY_CD='LH';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='O1' where CPTL_AST_CNTRY_CD='LI';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='O2' where CPTL_AST_CNTRY_CD='LO';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='O3' where CPTL_AST_CNTRY_CD='LQ';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='O4' where CPTL_AST_CNTRY_CD='LS';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='O5' where CPTL_AST_CNTRY_CD='LT';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='O8' where CPTL_AST_CNTRY_CD='MA';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='O9' where CPTL_AST_CNTRY_CD='MB';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='P0' where CPTL_AST_CNTRY_CD='MC';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='P2' where CPTL_AST_CNTRY_CD='MF';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='P3' where CPTL_AST_CNTRY_CD='MG';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='P4' where CPTL_AST_CNTRY_CD='MH';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='P5' where CPTL_AST_CNTRY_CD='MI';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='P8' where CPTL_AST_CNTRY_CD='MN';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='P9' where CPTL_AST_CNTRY_CD='MO';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='Q0' where CPTL_AST_CNTRY_CD='MP';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='Q1' where CPTL_AST_CNTRY_CD='MQ';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='Q4' where CPTL_AST_CNTRY_CD='MU';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='Q6' where CPTL_AST_CNTRY_CD='MW';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='R0' where CPTL_AST_CNTRY_CD='NA';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='R2' where CPTL_AST_CNTRY_CD='NE';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='R4' where CPTL_AST_CNTRY_CD='NG';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='R5' where CPTL_AST_CNTRY_CD='NH';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='R6' where CPTL_AST_CNTRY_CD='NI';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='S1' where CPTL_AST_CNTRY_CD='NS';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='S2' where CPTL_AST_CNTRY_CD='NU';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='S4' where CPTL_AST_CNTRY_CD='OC';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='S5' where CPTL_AST_CNTRY_CD='PA';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='S6' where CPTL_AST_CNTRY_CD='PC';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='S8' where CPTL_AST_CNTRY_CD='PF';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='S9' where CPTL_AST_CNTRY_CD='PG';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='T2' where CPTL_AST_CNTRY_CD='PM';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='T3' where CPTL_AST_CNTRY_CD='PO';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='T4' where CPTL_AST_CNTRY_CD='PP';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='T5' where CPTL_AST_CNTRY_CD='PS';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='T6' where CPTL_AST_CNTRY_CD='PU';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='T9' where CPTL_AST_CNTRY_CD='RM';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='U1' where CPTL_AST_CNTRY_CD='RP';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='U2' where CPTL_AST_CNTRY_CD='RQ';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='U3' where CPTL_AST_CNTRY_CD='RS';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='U6' where CPTL_AST_CNTRY_CD='SB';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='U7' where CPTL_AST_CNTRY_CD='SC';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='U8' where CPTL_AST_CNTRY_CD='SE';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='U9' where CPTL_AST_CNTRY_CD='SF';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='V0' where CPTL_AST_CNTRY_CD='SG';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='V5' where CPTL_AST_CNTRY_CD='SN';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='V7' where CPTL_AST_CNTRY_CD='SP';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='V8' where CPTL_AST_CNTRY_CD='SR';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='V9' where CPTL_AST_CNTRY_CD='ST';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='W0' where CPTL_AST_CNTRY_CD='SU';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='W1' where CPTL_AST_CNTRY_CD='SV';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='W2' where CPTL_AST_CNTRY_CD='SW';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='W4' where CPTL_AST_CNTRY_CD='SZ';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='W5' where CPTL_AST_CNTRY_CD='TC';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='W6' where CPTL_AST_CNTRY_CD='TD';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='W7' where CPTL_AST_CNTRY_CD='TE';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='W9' where CPTL_AST_CNTRY_CD='TI';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='X0' where CPTL_AST_CNTRY_CD='TK';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='X1' where CPTL_AST_CNTRY_CD='TL';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='X2' where CPTL_AST_CNTRY_CD='TN';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='X3' where CPTL_AST_CNTRY_CD='TO';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='X4' where CPTL_AST_CNTRY_CD='TP';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='X5' where CPTL_AST_CNTRY_CD='TS';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='X6' where CPTL_AST_CNTRY_CD='TU';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='X9' where CPTL_AST_CNTRY_CD='TX';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='Y2' where CPTL_AST_CNTRY_CD='UK';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='Y3' where CPTL_AST_CNTRY_CD='UP';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='Y4' where CPTL_AST_CNTRY_CD='UR';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='Z0' where CPTL_AST_CNTRY_CD='VI';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='Z1' where CPTL_AST_CNTRY_CD='VM';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='Z2' where CPTL_AST_CNTRY_CD='VQ';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='Z3' where CPTL_AST_CNTRY_CD='VT';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='Z4' where CPTL_AST_CNTRY_CD='WA';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='Z5' where CPTL_AST_CNTRY_CD='WE';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='Z7' where CPTL_AST_CNTRY_CD='WI';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='Z8' where CPTL_AST_CNTRY_CD='WQ';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='00' where CPTL_AST_CNTRY_CD='WZ';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='01' where CPTL_AST_CNTRY_CD='YM';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='02' where CPTL_AST_CNTRY_CD='YO';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='03' where CPTL_AST_CNTRY_CD='ZA';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='04' where CPTL_AST_CNTRY_CD='ZI';
-- Change to final code
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='AW' where CPTL_AST_CNTRY_CD='A0';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='AG' where CPTL_AST_CNTRY_CD='A1';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='DZ' where CPTL_AST_CNTRY_CD='A3';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='AZ' where CPTL_AST_CNTRY_CD='A4';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='AD' where CPTL_AST_CNTRY_CD='A7';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='AS' where CPTL_AST_CNTRY_CD='A9';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='AU' where CPTL_AST_CNTRY_CD='B1';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='AU' where CPTL_AST_CNTRY_CD='B2';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='AT' where CPTL_AST_CNTRY_CD='B3';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='AI' where CPTL_AST_CNTRY_CD='B4';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='AQ' where CPTL_AST_CNTRY_CD='B5';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='BH' where CPTL_AST_CNTRY_CD='B6';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='BW' where CPTL_AST_CNTRY_CD='B8';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='BM' where CPTL_AST_CNTRY_CD='B9';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='BS' where CPTL_AST_CNTRY_CD='C1';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='BD' where CPTL_AST_CNTRY_CD='C2';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='BZ' where CPTL_AST_CNTRY_CD='C3';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='BA' where CPTL_AST_CNTRY_CD='C4';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='BO' where CPTL_AST_CNTRY_CD='C5';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='MM' where CPTL_AST_CNTRY_CD='C6';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='BJ' where CPTL_AST_CNTRY_CD='C7';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='BY' where CPTL_AST_CNTRY_CD='C8';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='SB' where CPTL_AST_CNTRY_CD='C9';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='UM' where CPTL_AST_CNTRY_CD='D0';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='TF' where CPTL_AST_CNTRY_CD='D2';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='BG' where CPTL_AST_CNTRY_CD='D4';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='BN' where CPTL_AST_CNTRY_CD='D6';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='BI' where CPTL_AST_CNTRY_CD='D7';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='KH' where CPTL_AST_CNTRY_CD='D9';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='TD' where CPTL_AST_CNTRY_CD='E0';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='LK' where CPTL_AST_CNTRY_CD='E1';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='CG' where CPTL_AST_CNTRY_CD='E2';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='CD' where CPTL_AST_CNTRY_CD='E3';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='CN' where CPTL_AST_CNTRY_CD='E4';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='CL' where CPTL_AST_CNTRY_CD='E5';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='KY' where CPTL_AST_CNTRY_CD='E6';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='CC' where CPTL_AST_CNTRY_CD='E7';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='KM' where CPTL_AST_CNTRY_CD='E9';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='MP' where CPTL_AST_CNTRY_CD='F1';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='AU' where CPTL_AST_CNTRY_CD='F2';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='CR' where CPTL_AST_CNTRY_CD='F3';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='CF' where CPTL_AST_CNTRY_CD='F4';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='CK' where CPTL_AST_CNTRY_CD='F7';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='CS' where CPTL_AST_CNTRY_CD='F9';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='DK' where CPTL_AST_CNTRY_CD='G0';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='DM' where CPTL_AST_CNTRY_CD='G2';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='DO' where CPTL_AST_CNTRY_CD='G3';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='IE' where CPTL_AST_CNTRY_CD='G6';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='GQ' where CPTL_AST_CNTRY_CD='G7';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='EE' where CPTL_AST_CNTRY_CD='G8';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='SV' where CPTL_AST_CNTRY_CD='H0';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='TF' where CPTL_AST_CNTRY_CD='H2';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='CZ' where CPTL_AST_CNTRY_CD='H3';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='FK' where CPTL_AST_CNTRY_CD='H4';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='GF' where CPTL_AST_CNTRY_CD='H5';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='PF' where CPTL_AST_CNTRY_CD='I0';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='UM' where CPTL_AST_CNTRY_CD='I1';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='TF' where CPTL_AST_CNTRY_CD='I3';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='GM' where CPTL_AST_CNTRY_CD='I4';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='GA' where CPTL_AST_CNTRY_CD='I5';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='DE' where CPTL_AST_CNTRY_CD='I6';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='GE' where CPTL_AST_CNTRY_CD='I7';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='GD' where CPTL_AST_CNTRY_CD='J0';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='GG' where CPTL_AST_CNTRY_CD='J1';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='DE' where CPTL_AST_CNTRY_CD='J3';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='TF' where CPTL_AST_CNTRY_CD='J4';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='GU' where CPTL_AST_CNTRY_CD='J6';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='GN' where CPTL_AST_CNTRY_CD='J9';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='PS' where CPTL_AST_CNTRY_CD='K1';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='HT' where CPTL_AST_CNTRY_CD='K2';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='HN' where CPTL_AST_CNTRY_CD='K5';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='UM' where CPTL_AST_CNTRY_CD='K6';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='IS' where CPTL_AST_CNTRY_CD='K9';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='FR' where CPTL_AST_CNTRY_CD='L4';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='IL' where CPTL_AST_CNTRY_CD='L6';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='CI' where CPTL_AST_CNTRY_CD='L8';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='NT' where CPTL_AST_CNTRY_CD='L9';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='IQ' where CPTL_AST_CNTRY_CD='M0';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='JP' where CPTL_AST_CNTRY_CD='M1';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='NO' where CPTL_AST_CNTRY_CD='M4';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='UM' where CPTL_AST_CNTRY_CD='M6';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='TF' where CPTL_AST_CNTRY_CD='M7';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='KP' where CPTL_AST_CNTRY_CD='N0';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='UM' where CPTL_AST_CNTRY_CD='N1';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='KI' where CPTL_AST_CNTRY_CD='N2';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='KR' where CPTL_AST_CNTRY_CD='N3';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='CX' where CPTL_AST_CNTRY_CD='N4';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='KW' where CPTL_AST_CNTRY_CD='N5';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='LB' where CPTL_AST_CNTRY_CD='N8';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='LV' where CPTL_AST_CNTRY_CD='N9';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='LT' where CPTL_AST_CNTRY_CD='O0';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='LR' where CPTL_AST_CNTRY_CD='O1';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='SK' where CPTL_AST_CNTRY_CD='O2';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='UM' where CPTL_AST_CNTRY_CD='O3';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='LI' where CPTL_AST_CNTRY_CD='O4';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='LS' where CPTL_AST_CNTRY_CD='O5';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='MG' where CPTL_AST_CNTRY_CD='O8';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='MQ' where CPTL_AST_CNTRY_CD='O9';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='MO' where CPTL_AST_CNTRY_CD='P0';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='YT' where CPTL_AST_CNTRY_CD='P2';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='MN' where CPTL_AST_CNTRY_CD='P3';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='MS' where CPTL_AST_CNTRY_CD='P4';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='MW' where CPTL_AST_CNTRY_CD='P5';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='MC' where CPTL_AST_CNTRY_CD='P8';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='MA' where CPTL_AST_CNTRY_CD='P9';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='MU' where CPTL_AST_CNTRY_CD='Q0';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='UM' where CPTL_AST_CNTRY_CD='Q1';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='OM' where CPTL_AST_CNTRY_CD='Q4';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='ME' where CPTL_AST_CNTRY_CD='Q6';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='AN' where CPTL_AST_CNTRY_CD='R0';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='NU' where CPTL_AST_CNTRY_CD='R2';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='NE' where CPTL_AST_CNTRY_CD='R4';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='VU' where CPTL_AST_CNTRY_CD='R5';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='NG' where CPTL_AST_CNTRY_CD='R6';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='SR' where CPTL_AST_CNTRY_CD='S1';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='NI' where CPTL_AST_CNTRY_CD='S2';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='ZZ' where CPTL_AST_CNTRY_CD='S4';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='PY' where CPTL_AST_CNTRY_CD='S5';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='PN' where CPTL_AST_CNTRY_CD='S6';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='XP' where CPTL_AST_CNTRY_CD='S8';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='XS' where CPTL_AST_CNTRY_CD='S9';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='PA' where CPTL_AST_CNTRY_CD='T2';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='PT' where CPTL_AST_CNTRY_CD='T3';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='PG' where CPTL_AST_CNTRY_CD='T4';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='PW' where CPTL_AST_CNTRY_CD='T5';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='GW' where CPTL_AST_CNTRY_CD='T6';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='MH' where CPTL_AST_CNTRY_CD='T9';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='PH' where CPTL_AST_CNTRY_CD='U1';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='PR' where CPTL_AST_CNTRY_CD='U2';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='RU' where CPTL_AST_CNTRY_CD='U3';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='PM' where CPTL_AST_CNTRY_CD='U6';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='KN' where CPTL_AST_CNTRY_CD='U7';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='SC' where CPTL_AST_CNTRY_CD='U8';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='ZA' where CPTL_AST_CNTRY_CD='U9';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='SN' where CPTL_AST_CNTRY_CD='V0';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='SG' where CPTL_AST_CNTRY_CD='V5';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='ES' where CPTL_AST_CNTRY_CD='V7';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='RS' where CPTL_AST_CNTRY_CD='V8';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='LC' where CPTL_AST_CNTRY_CD='V9';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='SD' where CPTL_AST_CNTRY_CD='W0';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='SJ' where CPTL_AST_CNTRY_CD='W1';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='SE' where CPTL_AST_CNTRY_CD='W2';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='CH' where CPTL_AST_CNTRY_CD='W4';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='AE' where CPTL_AST_CNTRY_CD='W5';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='TT' where CPTL_AST_CNTRY_CD='W6';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='TF' where CPTL_AST_CNTRY_CD='W7';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='TJ' where CPTL_AST_CNTRY_CD='W9';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='TC' where CPTL_AST_CNTRY_CD='X0';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='TK' where CPTL_AST_CNTRY_CD='X1';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='TO' where CPTL_AST_CNTRY_CD='X2';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='TG' where CPTL_AST_CNTRY_CD='X3';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='ST' where CPTL_AST_CNTRY_CD='X4';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='TN' where CPTL_AST_CNTRY_CD='X5';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='TR' where CPTL_AST_CNTRY_CD='X6';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='TM' where CPTL_AST_CNTRY_CD='X9';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='GB' where CPTL_AST_CNTRY_CD='Y2';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='UA' where CPTL_AST_CNTRY_CD='Y3';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='SU' where CPTL_AST_CNTRY_CD='Y4';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='VG' where CPTL_AST_CNTRY_CD='Z0';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='VN' where CPTL_AST_CNTRY_CD='Z1';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='VI' where CPTL_AST_CNTRY_CD='Z2';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='VA' where CPTL_AST_CNTRY_CD='Z3';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='NA' where CPTL_AST_CNTRY_CD='Z4';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='PS' where CPTL_AST_CNTRY_CD='Z5';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='EH' where CPTL_AST_CNTRY_CD='Z7';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='UM' where CPTL_AST_CNTRY_CD='Z8';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='SZ' where CPTL_AST_CNTRY_CD='00';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='YE' where CPTL_AST_CNTRY_CD='01';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='YU' where CPTL_AST_CNTRY_CD='02';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='ZM' where CPTL_AST_CNTRY_CD='03';
UPDATE PUR_PO_CPTL_AST_LOC_T SET CPTL_AST_CNTRY_CD='ZW' where CPTL_AST_CNTRY_CD='04';

