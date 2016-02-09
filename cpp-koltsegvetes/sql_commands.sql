select tr_Id, 
  case cast (strftime('%w', tr_Datum) as integer)
    when 0 then 'Sunday'
    when 1 then 'Monday'
    when 2 then 'Tuesday'
    when 3 then 'Wednesday'
    when 4 then 'Thursday'
    when 5 then 'Friday'
    else 'Saturday' end as Datum, fs_Nev, tr_UjEgyenleg, tr_Osszeg, tr_Tipus, tr_Megjegyzes 
from Tranzakciok, Folyoszamlak 
where fs_Id == tr_SzamlaId;

select tr_Id, strftime('%Y.%m.%d. ', tr_Datum) ||
  case cast (strftime('%w', tr_Datum) as integer)
    when 0 then 'Hetfo'
    when 1 then 'Kedd'
    when 2 then 'Szerda'
    when 3 then 'Csutortok'
    when 4 then 'Pentek'
    when 5 then 'Szombat'
    else 'Vasarnap' end as Datum, fs_Nev, tr_UjEgyenleg, tr_Osszeg, tr_Tipus, tr_Megjegyzes 
from Tranzakciok, Folyoszamlak 
where fs_Id == tr_SzamlaId;

select tr_Id, strftime('%Y.%m.%d. ', tr_Datum) || 
  case cast (strftime('%w', tr_Datum) as integer) 
    when 0 then 'Hetfo'
    when 1 then 'Kedd'
    when 2 then 'Szerda'
    when 3 then 'Csutortok'
    when 4 then 'Pentek'
    when 5 then 'Szombat'
    else 'Vasarnap' end as Datum, 
  fs_Nev as Folyoszamla, 
  tr_UjEgyenleg as 'Uj Egyenleg', 
  tr_Osszeg as Osszeg, 
  tr_Tipus as Tipus, 
  tr_Megjegyzes as Megjegyzesek,
  fs_Id
from Tranzakciok, Folyoszamlak 
where fs_Id == tr_SzamlaId;

select tr_Id, strftime('%Y.%m.%d. ', tr_Datum) || 
  case cast (strftime('%w', tr_Datum) as integer) 
    when 0 then 'Hetfo'
    when 1 then 'Kedd'
    when 2 then 'Szerda'
    when 3 then 'Csutortok'
    when 4 then 'Pentek'
    when 5 then 'Szombat'
    else 'Vasarnap' end as Datum, 
  fs_Nev as Folyoszamla, 
  CASE WHEN tr_Tipus LIKE 'KI_%' THEN tr_UjEgyenleg + tr_Osszeg ELSE tr_UjEgyenleg - tr_Osszeg END as 'Regi Egyenleg',
  tr_Osszeg as Osszeg, 
  tr_UjEgyenleg as 'Uj Egyenleg', 
  tr_Tipus as Tipus, 
  tr_Megjegyzes as Megjegyzesek,
  fs_Id
from Tranzakciok, Folyoszamlak 
where fs_Id == tr_SzamlaId;

update Tranzakciok set tr_Datum = '2013-07-09', tr_Osszeg = 20000, tr_Megjegyzes = 'MKK tabori dij (16000) + egyeb koltsegek', tr_Tipus = 'BE_Akarmi', tr_SzamlaId = 'potp', tr_UjEgyenleg = 27300 where tr_Id == '4';

SELECT tr1.tr_Id, tr1.tr_UjEgyenleg, tr1.tr_Osszeg, tr2.tr_UjEgyenleg 
FROM Tranzakciok as tr1, Tranzakciok as tr2 
WHERE tr1.tr_Osszeg + tr1.tr_UjEgyenleg == tr2.tr_UjEgyenleg; 

-- nem az igazi
SELECT tr1.tr_Id, tr1.tr_Tipus, tr1.tr_UjEgyenleg, tr1.tr_Osszeg, tr2.tr_UjEgyenleg 
FROM Tranzakciok as tr1, Tranzakciok as tr2 
WHERE (tr1.tr_Tipus LIKE 'KI_%' AND tr1.tr_UjEgyenleg + tr1.tr_Osszeg == tr2.tr_UjEgyenleg) 
   OR (NOT (tr1.tr_Tipus LIKE 'KI_%') AND tr1.tr_UjEgyenleg - tr1.tr_Osszeg == tr2.tr_UjEgyenleg);

-- lekerdezes amellyel ellenorizni tudom, hogy jol mukodik-e a szamla levezetese
-- EZ EGY HASZNOS LEKERDEZES
SELECT tr1.tr_Id || "  " || tr1.tr_SzamlaId || ", " || tr1.tr_Tipus || ": " ||
       tr0.tr_UjEgyenleg || CASE WHEN tr1.tr_Tipus LIKE 'KI_%' THEN " - " ELSE " + " END || tr1.tr_Osszeg || " = " || tr1.tr_UjEgyenleg
FROM Tranzakciok as tr1, Tranzakciok as tr0 
WHERE ( 
        (tr1.tr_Tipus LIKE 'KI_%' AND tr1.tr_UjEgyenleg + tr1.tr_Osszeg == tr0.tr_UjEgyenleg) 
        OR
        (NOT (tr1.tr_Tipus LIKE 'KI_%') AND tr1.tr_UjEgyenleg - tr1.tr_Osszeg == tr0.tr_UjEgyenleg)
      ) 
      AND
      (
        tr1.tr_SzamlaId == tr0.tr_SzamlaId
      )
GROUP BY tr1.tr_Id
ORDER BY tr1.tr_Datum ASC
-- tr1.tr_Tipus ASC, 
;

SELECT tr_Id, tr_Tipus, tr_SzamlaId, tr_UjEgyenleg, tr_Osszeg, tr_UjEgyenleg + tr_Osszeg 
FROM Tranzakciok;


   